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
                    .withClasspathResourceMapping("db/ainitscript.sql", "/docker-entrypoint-initdb.d/ainitscript.sql", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("db/warehouse.sql", "/docker-entrypoint-initdb.d/warehouse.sql", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("db/company.sql", "/docker-entrypoint-initdb.d/company.sql", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("db/user.sql", "/docker-entrypoint-initdb.d/user.sql", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("db/zpost.sql", "/docker-entrypoint-initdb.d/zpost.sql", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("db/inventory-items.sql", "/docker-entrypoint-initdb.d/inventory-items.sql", BindMode.READ_ONLY)
                    .withClasspathResourceMapping("db/supplier.sql", "/docker-entrypoint-initdb.d/supplier.sql", BindMode.READ_ONLY);

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
