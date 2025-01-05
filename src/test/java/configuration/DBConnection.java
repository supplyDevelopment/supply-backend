package configuration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class DBConnection {

    @Container
    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass")
                    .withClasspathResourceMapping("db/changelog/v-1.0/01-types.sql", "/docker-entrypoint-initdb.d/01-types.sql", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("db/changelog/v-1.0/02-users.sql", "/docker-entrypoint-initdb.d/02-users.sql", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("db/changelog/v-1.0/03-resources.sql", "/docker-entrypoint-initdb.d/03-resources.sql", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("db/changelog/v-1.0/04-warehouses.sql", "/docker-entrypoint-initdb.d/04-warehouses.sql", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("db/changelog/v-1.0/05-operations.sql", "/docker-entrypoint-initdb.d/05-operations.sql", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("db/changelog/v-1.0/06-company.sql", "/docker-entrypoint-initdb.d/06-company.sql", BindMode.READ_ONLY);



    static {
        POSTGRESQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }
}
