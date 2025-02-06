package supply.server.data.history;

import org.junit.jupiter.api.Test;
import supply.server.configuration.DataCreator;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.resource.CreateResource;
import supply.server.data.resource.EditResource;
import supply.server.data.resource.Resource;
import supply.server.data.resource.RpResource;
import supply.server.data.resource.history.CreateHistory;
import supply.server.data.resource.history.ResourceHistory;
import supply.server.data.resource.history.RpResourceHistory;
import supply.server.data.user.User;
import supply.server.data.warehouse.RpWarehouse;
import supply.server.data.warehouse.Warehouse;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RpHistoryTest extends DataCreator {

    private final DataSource dataSource = dataSource();

    @Test
    void addTest() throws SQLException {
        RpResourceHistory rpHistory = new RpResourceHistory(dataSource);
        EditResource editResource = new EditResource(
                2,
                Optional.of("test"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
        RpResource rpResource = new RpResource(dataSource);
        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);
        UUID companyId = getCompany(false).id();
        Warehouse warehouse = rpWarehouse.add(generateWarehouse(getUsers(2, false).stream().map(User::id).toList()), companyId).orElseThrow();

        CreateResource createResource = generateResource(getUser(false).id(), warehouse.id(), getProject(false).id());
        Resource expected = rpResource.add(createResource).orElseThrow();

        CreateResource updated = editResource.edit(expected);
        Resource actual = rpResource.add(updated).orElseThrow();

        CreateHistory createHistory = new CreateHistory(editResource, expected, actual);

        ResourceHistory resourceHistory = rpHistory.add(createHistory, companyId).orElseThrow();

        assertEquals(createHistory.quantity(), resourceHistory.quantity());
        assertEquals(resourceHistory.prev_id(), expected.id());
        assertEquals(resourceHistory.goal_id(), actual.id());
        assertEquals(resourceHistory.prev_name(), expected.name());
        assertEquals(resourceHistory.goal_name(), actual.name());
        assertEquals(5, resourceHistory.prev_count());
        assertEquals(2, resourceHistory.goal_count());
        assertNull(resourceHistory.prev_status());
        assertNull(resourceHistory.goal_status());
        assertNull(resourceHistory.prev_projectId());
        assertNull(resourceHistory.goal_projectId());
        assertNull(resourceHistory.prev_description());
        assertNull(resourceHistory.goal_description());
        assertNull(resourceHistory.prev_warehouseId());
        assertNull(resourceHistory.goal_warehouseId());
        assertNull(resourceHistory.prev_userId());
        assertNull(resourceHistory.goal_userId());

        PaginatedList<ResourceHistory> actualHistory = rpHistory.get(expected.id(), companyId, new Pagination(10, 0));
        assertEquals(resourceHistory, actualHistory.items().get(0));
    }

}
