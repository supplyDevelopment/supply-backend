package supply.server.data.resource;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import supply.server.configuration.DBConnection;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.company.CreateCompany;
import supply.server.data.company.RpCompany;
import supply.server.data.project.Project;
import supply.server.data.project.RpProject;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.Unit;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;
import supply.server.data.warehouse.CreateWarehouse;
import supply.server.data.warehouse.RpWarehouse;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RpResourceTest extends DBConnection {

    private final DataSource dataSource = dataSource();

    // TODO check ability to use project an warehouse
    @Test
    void addTest() throws SQLException, MalformedURLException {
        RpProject rpProject = new RpProject(dataSource);
        Project project = rpProject.add("testName", "testDescription", getCompanyId()).orElseThrow();

        RpResource rpResource = new RpResource(dataSource);
        CreateResource createResource = new CreateResource(
                List.of(new URL("http://test.com")),
                "testName",
                1,
                Unit.KG,
                ResourceType.PRODUCT,
                getUserId(),
                getWarehouseId(),
                project.id(),
                ResourceStatus.ACTIVE,
                "testDescription"
                );

        Resource resource = rpResource.add(createResource).orElseThrow();

        assertEquals(createResource.name(), resource.name());
        assertEquals(createResource.count(), resource.count());
        assertEquals(createResource.unit(), resource.unit());
        assertEquals(createResource.type(), resource.type());
        assertEquals(createResource.projectId(), resource.projectId());
        assertEquals(createResource.status(), resource.status());
        assertEquals(createResource.description(), resource.description());

        JdbcSession jdbcSession = new JdbcSession(dataSource);

        UUID companyFromWarehouse = jdbcSession
                .sql("""
                        SELECT company
                        FROM company_warehouses
                        WHERE warehouse = ?
                        """)
                .set(resource.warehouseId())
                .select((rset, stmt) -> {
                    if (rset.next()) return rset.getObject("company", UUID.class);
                    return null;
                });

        Resource insertedResource = jdbcSession
                .sql("""
                        SELECT r.id, r.images, r.name, r.count, r.unit, r.type, r.projectId,
                               r.status, r.description, r.warehouseId, r.created_at, r.updated_at,
                               ru.user_id AS user_id
                        FROM resource r
                        LEFT JOIN resource_users ru ON r.id = ru.resource_id
                        WHERE r.id = ?
                        """)
                .set(resource.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        Array imagesArray = rset.getArray("images");
                        List<URL> images = null;

                        if (imagesArray != null) {
                            images = Arrays.stream((String[]) imagesArray.getArray())
                                    .map(url -> {
                                        try {
                                            return new URL(url);
                                        } catch (MalformedURLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                                    .toList();
                        }

                        return new Resource(
                                rset.getObject("id", UUID.class),
                                images,
                                rset.getString("name"),
                                rset.getInt("count"),
                                Unit.valueOf(rset.getString("unit")),
                                ResourceType.valueOf(rset.getString("type")),
                                rset.getObject("projectId", UUID.class),
                                ResourceStatus.valueOf(rset.getString("status")),
                                rset.getString("description"),
                                rset.getObject("warehouseId", UUID.class),
                                rset.getObject("user_id", UUID.class),
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate()
                        );
                    }
                    return null;
                });

        assertEquals(insertedResource.id(), resource.id());
        assertEquals(insertedResource.images().get(0), resource.images().get(0));
        assertEquals(insertedResource.name(), resource.name());
        assertEquals(insertedResource.count(), resource.count());
        assertEquals(insertedResource.unit(), resource.unit());
        assertEquals(insertedResource.type(), resource.type());
        assertEquals(insertedResource.projectId(), resource.projectId());
        assertEquals(insertedResource.warehouseId(), resource.warehouseId());
        assertEquals(insertedResource.userId(), resource.userId());
        assertEquals(insertedResource.status(), resource.status());
        assertEquals(insertedResource.description(), resource.description());
        assertEquals(insertedResource.createdAt(), resource.createdAt());
        assertEquals(insertedResource.updatedAt(), resource.updatedAt());
    }

    @Test
    void getTest() throws SQLException, MalformedURLException {
        RpProject rpProject = new RpProject(dataSource);
        Project project = rpProject.add("testName", "testDescription", getCompanyId()).orElseThrow();

        RpResource rpResource = new RpResource(dataSource);
        CreateResource createResource = new CreateResource(
                List.of(new URL("http://test.com")),
                "testName",
                1,
                Unit.KG,
                ResourceType.PRODUCT,
                getUserId(),
                getWarehouseId(),
                project.id(),
                ResourceStatus.ACTIVE,
                "testDescription"
        );

        Resource expected = rpResource.add(createResource).orElseThrow();
        Resource actual = rpResource.get(expected.id(), getCompanyId()).orElseThrow();

        assertEquals(expected.id(), actual.id());
        assertEquals(expected.images().get(0), actual.images().get(0));
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.count(), actual.count());
        assertEquals(expected.unit(), actual.unit());
        assertEquals(expected.type(), actual.type());
        assertEquals(expected.projectId(), actual.projectId());
        assertEquals(expected.warehouseId(), actual.warehouseId());
        assertEquals(expected.userId(), actual.userId());
        assertEquals(expected.status(), actual.status());
        assertEquals(expected.description(), actual.description());
        assertEquals(expected.createdAt(), actual.createdAt());
        assertEquals(expected.updatedAt(), actual.updatedAt());

        assertTrue(rpResource.get(expected.id(), UUID.randomUUID()).isEmpty());
    }

    @Order(0)
    @Test
    void getAllTest() throws SQLException, MalformedURLException {
        UUID warehouse1Id = getWarehouseId();
        UUID company1Id = getCompanyId();
        RpCompany rpCompany = new RpCompany(dataSource);
        UUID company2Id = rpCompany.add(new CreateCompany(
                "tes1t",
                List.of(new Email("e@e.com")),
                List.of(new Phone("+79033073746")),
                new Bil("1234"),
                new Tax("1234"),
                List.of(new Address("test")),
                CompanyStatus.ACTIVE
        )).orElseThrow().id();

        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);
        UUID warehouse2Id = rpWarehouse.add(new CreateWarehouse(
                "test",
                new Address("test"),
                0l,
                0l,
                List.of(getUserId())
        ), company2Id).orElseThrow().id();

        RpResource rpResource = new RpResource(dataSource);
        List<Resource> expected = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            if (i % 4 == 0) {
                expected.add(rpResource.add(new CreateResource(
                        List.of(new URL("http://test.com")),
                        "adding" + i,
                        1,
                        Unit.KG,
                        ResourceType.PRODUCT,
                        getUserId(),
                        warehouse1Id,
                        getProjectId(),
                        ResourceStatus.ACTIVE,
                        "testDescription"
                )).orElseThrow());
            } else if (i % 2 == 0) {
                rpResource.add(new CreateResource(
                        List.of(new URL("http://test.com")),
                        "agetting" + i,
                        1,
                        Unit.KG,
                        ResourceType.PRODUCT,
                        getUserId(),
                        warehouse2Id,
                        getProjectId(),
                        ResourceStatus.ACTIVE,
                        "testDescription"
                )).orElseThrow();
            } else {
                rpResource.add(new CreateResource(
                        List.of(new URL("http://test.com")),
                        "agetting" + i,
                        1,
                        Unit.KG,
                        ResourceType.PRODUCT,
                        getUserId(),
                        warehouse1Id,
                        getProjectId(),
                        ResourceStatus.ACTIVE,
                        "testDescription"
                )).orElseThrow();
            }
        }

        PaginatedList<Resource> actual = rpResource.getAll("adding", company1Id, new Pagination(20, 0));
        assertEquals(4, actual.total());
        for (Resource resource : actual.items()) {
            for (Resource expectedResource : expected) {
                if (expectedResource.id().equals(resource.id())) {
                    assertEquals(expectedResource.id(), resource.id());
                    assertEquals(expectedResource.images().get(0), resource.images().get(0));
                    assertEquals(expectedResource.name(), resource.name());
                    assertEquals(expectedResource.count(), resource.count());
                    assertEquals(expectedResource.unit(), resource.unit());
                    assertEquals(expectedResource.type(), resource.type());
                    assertEquals(expectedResource.projectId(), resource.projectId());
                    assertEquals(expectedResource.warehouseId(), resource.warehouseId());
                    assertEquals(expectedResource.userId(), resource.userId());
                    assertEquals(expectedResource.status(), resource.status());
                    assertEquals(expectedResource.description(), resource.description());
                    assertEquals(expectedResource.createdAt(), resource.createdAt());
                    assertEquals(expectedResource.updatedAt(), resource.updatedAt());
                }
            }
        }

        assertEquals(12, rpResource.getAll("a", company1Id, new Pagination(20, 0)).total());
        assertEquals(12, rpResource.getAll("", company1Id, new Pagination(20, 0)).total());
    }

    @Test
    void editNoParametersTest() throws SQLException, MalformedURLException {
        RpProject rpProject = new RpProject(dataSource);
        Project project = rpProject.add("testName", "testDescription", getCompanyId()).orElseThrow();

        RpResource rpResource = new RpResource(dataSource);
        CreateResource createResource = new CreateResource(
                List.of(new URL("http://test.com")),
                "testName",
                1,
                Unit.KG,
                ResourceType.PRODUCT,
                getUserId(),
                getWarehouseId(),
                project.id(),
                ResourceStatus.ACTIVE,
                "testDescription"
        );

        Resource resource = rpResource.add(createResource).orElseThrow();

        Resource editedResource = rpResource.edit(
                resource.id(),
                getCompanyId(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()).orElseThrow();

        assertEquals(resource.id(), editedResource.id());
        assertEquals(resource.images().get(0), editedResource.images().get(0));
        assertEquals(resource.name(), editedResource.name());
        assertEquals(resource.count(), editedResource.count());
        assertEquals(resource.unit(), editedResource.unit());
        assertEquals(resource.type(), editedResource.type());
        assertEquals(resource.projectId(), editedResource.projectId());
        assertEquals(resource.status(), editedResource.status());
        assertEquals(resource.description(), editedResource.description());

        assertTrue(rpResource.edit(resource.id(),
                UUID.randomUUID(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()).isEmpty());
    }

    @Test
    void editTest() throws SQLException, MalformedURLException {
        RpProject rpProject = new RpProject(dataSource);
        Project project = rpProject.add("testName", "testDescription", getCompanyId()).orElseThrow();

        RpResource rpResource = new RpResource(dataSource);
        CreateResource createResource = new CreateResource(
                List.of(new URL("http://test.com")),
                "testName",
                1,
                Unit.KG,
                ResourceType.PRODUCT,
                getUserId(),
                getWarehouseId(),
                project.id(),
                ResourceStatus.ACTIVE,
                "testDescription"
        );

        Resource resource = rpResource.add(createResource).orElseThrow();

        Resource editedResource = rpResource.edit(
                resource.id(),
                getCompanyId(),
                Optional.of("editedName"),
                Optional.of(2),
                Optional.empty(),
                Optional.empty(),
                Optional.of("editedDescription")).orElseThrow();

        assertEquals(resource.id(), editedResource.id());
        assertEquals(resource.images().get(0), editedResource.images().get(0));
        assertEquals("editedName", editedResource.name());
        assertEquals(2, editedResource.count());
        assertEquals(resource.unit(), editedResource.unit());
        assertEquals(resource.type(), editedResource.type());
        assertEquals(resource.projectId(), editedResource.projectId());
        assertEquals(resource.status(), editedResource.status());
        assertEquals("editedDescription", editedResource.description());
    }

}
