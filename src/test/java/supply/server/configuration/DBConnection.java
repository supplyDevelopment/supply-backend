package supply.server.configuration;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import supply.server.configuration.liquibase.LiquibaseRunner;
import supply.server.data.company.Company;
import supply.server.data.company.CreateCompany;
import supply.server.data.company.RpCompany;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DBConnection {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    protected final DataSource dataSource;

    public DBConnection() {
        PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource();
        pgSimpleDataSource.setUrl(postgres.getJdbcUrl());
        pgSimpleDataSource.setUser(postgres.getUsername());
        pgSimpleDataSource.setPassword(postgres.getPassword());
        this.dataSource = pgSimpleDataSource;
    }


    @BeforeAll
    static void beforeAll() throws SQLException {
        postgres.start();
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            new LiquibaseRunner("db/changelog/master.xml").run(connection);
        }
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    static Connection connection() throws SQLException {
        return DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    public DataSource dataSource() {
        return dataSource;
    }

    protected UUID getCompanyId() throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Optional<UUID> companyId = jdbcSession
                .sql("""
                        SELECT id FROM company
                        """)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(UUID.fromString(rset.getString("id")));
                    }
                    return Optional.<UUID>empty();
                });
        if (companyId.isEmpty()) {
            RpCompany rpCompany = new RpCompany(dataSource);
            CreateCompany createCompany = new CreateCompany(
                    "addTestCompany",
                    List.of(new Email("example@example.com")),
                    List.of(new Phone("+71234567890")),
                    new Bil("1234567890"),
                    new Tax("1234567890"),
                    List.of(new Address("test")),
                    CompanyStatus.ACTIVE
            );
            Company company = rpCompany.add(createCompany).orElseThrow();
            return company.id();
        } else {
            return companyId.get();
        }
    }

}
