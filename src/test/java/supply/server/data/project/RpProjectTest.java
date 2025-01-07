package supply.server.data.project;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Test;
import supply.server.configuration.DBConnection;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RpProjectTest extends DBConnection {

    private final DataSource dataSource = dataSource();

    @Test
    void addTest() throws SQLException {
        RpProject rpProject = new RpProject(dataSource);
        String name = "name";
        String description = "description";
        UUID companyId = UUID.randomUUID();

        Project insertedProject = rpProject.add(name, description, companyId).orElseThrow();

        assertEquals(name, insertedProject.name());
        assertEquals(description, insertedProject.description());
        assertEquals(companyId, insertedProject.companyId());

        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Project project = jdbcSession.sql("SELECT * FROM project WHERE id = ?")
                .set(insertedProject.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return new Project(
                                rset.getObject("id", UUID.class),
                                rset.getString("name"),
                                rset.getString("description"),
                                null,
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate()
                        );
                    }
                    return null;
                });

        assertEquals(insertedProject.id(), project.id());
        assertEquals(insertedProject.name(), project.name());
        assertEquals(insertedProject.description(), project.description());
        assertEquals(insertedProject.createdAt(), project.createdAt());
        assertEquals(insertedProject.updatedAt(), project.updatedAt());
    }

    @Test
    void getTest() throws SQLException {
        RpProject rpProject = new RpProject(dataSource);
        String name = "getTestName";
        String description = "getTestDescription";
        UUID companyId = UUID.randomUUID();

        Project expected = rpProject.add(name, description, companyId).orElseThrow();
        Project actual = rpProject.get(expected.id()).orElseThrow();

        assertEquals(expected.id(), actual.id());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.description(), actual.description());
        assertEquals(expected.createdAt(), actual.createdAt());
        assertEquals(expected.updatedAt(), actual.updatedAt());
    }

}
