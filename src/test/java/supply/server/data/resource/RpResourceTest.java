package supply.server.data.resource;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import supply.server.configuration.DataCreator;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;
import supply.server.data.warehouse.Warehouse;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RpResourceTest extends DataCreator {

    private final DataSource dataSource = dataSource();

    @Test
    void addTest() throws SQLException {
        RpResource rpResource = new RpResource(dataSource);
        CreateResource createResource = generateResource(
                getUser(true).id(),
                getWarehouse(true).id(),
                getProject(true).id()
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
    void getTest() throws SQLException {
        RpResource rpResource = new RpResource(dataSource);

        Warehouse warehouse = getWarehouse(true);

        CreateResource createResource = generateResource(
                getUser(false).id(),
                warehouse.id(),
                getProject(false).id()
        );

        Resource expected = rpResource.add(createResource).orElseThrow();
        Resource actual = rpResource.get(expected.id(), warehouse.companyId()).orElseThrow();

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

    @Test
    void getAllTest() throws SQLException, MalformedURLException {
        List<Warehouse> warehouseIds = getWarehouses(2,true);

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
                        getUser(false).id(),
                        warehouseIds.get(0).id(),
                        getProject(false).id(),
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
                        getUser(false).id(),
                        warehouseIds.get(1).id(),
                        getProject(false).id(),
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
                        getUser(false).id(),
                        warehouseIds.get(0).id(),
                        getProject(false).id(),
                        ResourceStatus.ACTIVE,
                        "testDescription"
                )).orElseThrow();
            }
        }

        PaginatedList<Resource> actual = rpResource.getAll("adding", warehouseIds.get(0).companyId(), new Pagination(20, 0));
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

        assertEquals(12, rpResource.getAll("a", warehouseIds.get(0).companyId(), new Pagination(20, 0)).total());
        assertEquals(12, rpResource.getAll("", warehouseIds.get(0).companyId(), new Pagination(20, 0)).total());
    }

    @Test
    void editNoParametersTest() throws SQLException {
        Warehouse warehouse = getWarehouse(true);

        RpResource rpResource = new RpResource(dataSource);
        CreateResource createResource = generateResource(
                getUser(false).id(),
                warehouse.id(),
                getProject(false).id()
        );

        Resource resource = rpResource.add(createResource).orElseThrow();

        Resource editedResource = rpResource.edit(
                resource.id(),
                warehouse.companyId(),
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
    void editTest() throws SQLException {
        Warehouse warehouse = getWarehouse(true);

        RpResource rpResource = new RpResource(dataSource);
        CreateResource createResource = generateResource(
                getUser(false).id(),
                warehouse.id(),
                getProject(false).id()
        );

        Resource resource = rpResource.add(createResource).orElseThrow();

        Resource editedResource = rpResource.edit(
                resource.id(),
                warehouse.companyId(),
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
