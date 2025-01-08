package supply.server.data.resource;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import supply.server.configuration.DBConnection;
import supply.server.data.project.Project;
import supply.server.data.project.RpProject;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RpResourceTest extends DBConnection {

    private final DataSource dataSource = dataSource();

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

        UUID companyFromResource = jdbcSession
                .sql("""
                        SELECT company
                        FROM company_resources
                        WHERE resource = ?
                        """)
                .set(resource.id())
                .select((rset, stmt) -> {
                    if (rset.next()) return rset.getObject("company", UUID.class);
                    return null;
                });

        assertEquals(companyFromWarehouse, companyFromResource);


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
        Resource actual = rpResource.get(expected.id()).orElseThrow();

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
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        assertEquals(resource.id(), editedResource.id());
        assertEquals(resource.images().get(0), editedResource.images().get(0));
        assertEquals(resource.name(), editedResource.name());
        assertEquals(resource.count(), editedResource.count());
        assertEquals(resource.unit(), editedResource.unit());
        assertEquals(resource.type(), editedResource.type());
        assertEquals(resource.projectId(), editedResource.projectId());
        assertEquals(resource.status(), editedResource.status());
        assertEquals(resource.description(), editedResource.description());
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
                Optional.of("editedName"),
                Optional.of(2),
                Optional.empty(),
                Optional.empty(),
                Optional.of("editedDescription"));

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
