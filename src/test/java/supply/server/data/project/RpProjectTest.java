package supply.server.data.project;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Test;
import supply.server.configuration.DataCreator;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.company.Company;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RpProjectTest extends DataCreator {

    private final DataSource dataSource = dataSource();

    @Test
    void addTest() throws SQLException {
        RpProject rpProject = new RpProject(dataSource);
        String name = "name";
        String description = "description";
        UUID companyId = getCompany(false).id();

        Project insertedProject = rpProject.add(name, description, companyId).orElseThrow();

        assertEquals(name, insertedProject.name());
        assertEquals(description, insertedProject.description());
        assertEquals(companyId, insertedProject.companyId());

        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Project project = jdbcSession
                .sql("""
                        SELECT p.id, p.name, p.description, p.created_at, p.updated_at,
                               cp.company AS company_id
                        FROM project p
                        LEFT JOIN company_projects cp ON p.id = cp.project
                        WHERE p.id = ?
                        """)
                .set(insertedProject.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return new Project(
                                rset.getObject("id", UUID.class),
                                rset.getString("name"),
                                rset.getString("description"),
                                rset.getObject("company_id", UUID.class),
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate()
                        );
                    }
                    return null;
                });

        assertEquals(insertedProject.id(), project.id());
        assertEquals(insertedProject.name(), project.name());
        assertEquals(insertedProject.description(), project.description());
        assertEquals(insertedProject.companyId(), project.companyId());
        assertEquals(insertedProject.createdAt(), project.createdAt());
        assertEquals(insertedProject.updatedAt(), project.updatedAt());
    }

    @Test
    void getTest() throws SQLException {
        RpProject rpProject = new RpProject(dataSource);
        UUID companyId = getCompany(false).id();

        Project expected = getProject(false);
        Project actual = rpProject.get(expected.id(), companyId).orElseThrow();

        assertEquals(expected.id(), actual.id());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.description(), actual.description());
        assertEquals(expected.companyId(), actual.companyId());
        assertEquals(expected.createdAt(), actual.createdAt());
        assertEquals(expected.updatedAt(), actual.updatedAt());

        assertTrue(rpProject.get(expected.id(), UUID.randomUUID()).isEmpty());
    }

    @Test
    void getAllTest() throws SQLException {
        List<UUID> companyIds = getCompanies(2, true).stream().map(Company::id).toList();

        RpProject rpProject = new RpProject(dataSource);

        List<String> names = List.of(
                "addition",
                "agetting"
        );

        List<Project> expected = new ArrayList<>();
        long expectedSize = 0;
        for (int i = 0; i < 16; i++) {
            if (i % 4 == 0) {
                expectedSize++;
                expected.add(rpProject.add(names.get(0) + i, "description", companyIds.get(0)).orElseThrow());
            } else if (i % 2 == 0) {
                rpProject.add(names.get(0) + i, "description", companyIds.get(1)).orElseThrow();
            } else {
                rpProject.add(names.get(1) + i, "description", companyIds.get(0)).orElseThrow();
            }
        }

        PaginatedList<Project> actual = rpProject.getAll(names.get(0), companyIds.get(0), new Pagination(20, 0));
        assertEquals(expectedSize, actual.total());
        for (Project actualProject : actual.items()) {
            for (Project expectedProject : expected) {
                if (expectedProject.id().equals(actualProject.id())) {
                    assertEquals(expectedProject.id(), actualProject.id());
                    assertEquals(expectedProject.name(), actualProject.name());
                    assertEquals(expectedProject.description(), actualProject.description());
                    assertEquals(expectedProject.companyId(), actualProject.companyId());
                    assertEquals(expectedProject.createdAt(), actualProject.createdAt());
                    assertEquals(expectedProject.updatedAt(), actualProject.updatedAt());
                }
            }
        }

        assertEquals(0, rpProject.getAll(names.get(1), companyIds.get(1), new Pagination(20, 0)).total());
        assertEquals(12, rpProject.getAll("a", companyIds.get(0), new Pagination(20, 0)).total());
        assertEquals(12, rpProject.getAll("", companyIds.get(0), new Pagination(20, 0)).total());
    }


}
