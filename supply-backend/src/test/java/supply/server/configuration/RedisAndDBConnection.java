package supply.server.configuration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import supply.server.configuration.liquibase.LiquibaseRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RedisAndDBConnection {

    static GenericContainer<?> redisContainer;
    protected RedisTemplate<String, Object> redisTemplate;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );
    protected final DataSource dataSource;

    @BeforeAll
    static void beforeAll() throws SQLException {
        redisContainer = new GenericContainer<>("redis:7-alpine")
                .withExposedPorts(6379);
        redisContainer.start();
        postgres.start();
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            new LiquibaseRunner("db/changelog/master.xml").run(connection);
        }
    }

    @AfterAll
    static void afterAll() {
        redisContainer.stop();
        postgres.stop();
    }

    static Connection connection() throws SQLException {
        return DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    public DataSource dataSource() {
        return dataSource;
    }

    public RedisAndDBConnection() {
        String redisHost = redisContainer.getHost();
        Integer redisPort = redisContainer.getMappedPort(6379);

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(redisHost, redisPort)
        );
        connectionFactory.afterPropertiesSet();
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setEnableTransactionSupport(false);

        template.afterPropertiesSet();
        this.redisTemplate = template;

        PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource();
        pgSimpleDataSource.setUrl(postgres.getJdbcUrl());
        pgSimpleDataSource.setUser(postgres.getUsername());
        pgSimpleDataSource.setPassword(postgres.getPassword());
        this.dataSource = pgSimpleDataSource;
    }

}
