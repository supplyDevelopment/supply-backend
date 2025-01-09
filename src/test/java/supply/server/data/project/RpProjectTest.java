package supply.server.data.project;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Test;
import supply.server.configuration.DBConnection;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RpProjectTest extends DBConnection {

    private final DataSource dataSource = dataSource();

    @Test
    void addTest() throws SQLException {
        RpProject rpProject = new RpProject(dataSource);
        String name = "name";
        String description = "description";
        UUID companyId = getCompanyId();

        Project insertedProject = rpProject.add(name, description, companyId).orElseThrow();

        assertEquals(name, insertedProject.name());
        assertEquals(description, insertedProject.description());
        assertEquals(companyId, insertedProject.companyId());

        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Project project = jdbcSession.sql("""
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
        String name = "getTestName";
        String description = "getTestDescription";
        UUID companyId = getCompanyId();

        Project expected = rpProject.add(name, description, companyId).orElseThrow();
        Project actual = rpProject.get(expected.id()).orElseThrow();

        assertEquals(expected.id(), actual.id());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.description(), actual.description());
        assertEquals(expected.companyId(), actual.companyId());
        assertEquals(expected.createdAt(), actual.createdAt());
        assertEquals(expected.updatedAt(), actual.updatedAt());
    }

    @Test
    void getAllTest() throws SQLException {
        RpCompany rpCompany = new RpCompany(dataSource);
        UUID company1Id = getCompanyId();
        UUID company2Id = rpCompany.add(new CreateCompany(
                        "secondTestCompany",
                        List.of(new Email("exampl1e@example.com")),
                        List.of(new Phone("+71234567891")),
                        new Bil("1234567891"),
                        new Tax("1234567891"),
                        List.of(new Address("test1")),
                        CompanyStatus.ACTIVE
                )).map(Company::id).orElseThrow();

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
                expected.add(rpProject.add(names.get(0) + i, "description", company1Id).orElseThrow());
            } else if (i % 2 == 0) {
                rpProject.add(names.get(0) + i, "description", company2Id).orElseThrow();
            } else {
                rpProject.add(names.get(1) + i, "description", company1Id).orElseThrow();
            }
        }

        PaginatedList<Project> actual = rpProject.getAll(names.get(0), company1Id, new Pagination(20, 0));
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

        assertEquals(0, rpProject.getAll(names.get(1), company2Id, new Pagination(20, 0)).total());
        assertEquals(12, rpProject.getAll("a", company1Id, new Pagination(20, 0)).total());
        assertEquals(14, rpProject.getAll("", company1Id, new Pagination(20, 0)).total());
    }


}
