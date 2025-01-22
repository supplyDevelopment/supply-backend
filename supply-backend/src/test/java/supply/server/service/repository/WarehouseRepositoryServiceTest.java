package supply.server.service.repository;

import org.junit.jupiter.api.Test;
import supply.server.configuration.DataCreator;
import supply.server.configuration.exception.DataNotFoundException;
import supply.server.data.Redis;
import supply.server.data.user.User;
import supply.server.data.warehouse.CreateWarehouse;
import supply.server.data.warehouse.RpWarehouse;
import supply.server.data.warehouse.Warehouse;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class WarehouseRepositoryServiceTest extends DataCreator {

    private final Redis<Warehouse> inMemoryRpWarehouse = new Redis<>(redisTemplate, "warehouse:");
    private final RpWarehouse rpWarehouse = new RpWarehouse(dataSource);

    private final WarehouseRepositoryService warehouseService = new WarehouseRepositoryService(rpWarehouse, inMemoryRpWarehouse);

    @Test
    void addAndGetTest() throws SQLException {
        UUID companyId = getCompany(true).id();
        List<UUID> userIds = getUsers(2, true).stream().map(User::id).toList();
        CreateWarehouse createWarehouse = generateWarehouse(userIds);

        Warehouse warehouse = warehouseService.add(createWarehouse, companyId);
        checkEquality(createWarehouse, warehouse);

        Warehouse actual1 = warehouseService.get(warehouse.id(), companyId);
        checkEquality(warehouse, actual1);

        Warehouse actual2 = rpWarehouse.get(warehouse.id(), companyId).orElseThrow();
        checkEquality(warehouse, actual2);

        Warehouse actual3 = inMemoryRpWarehouse.get(warehouse.id()).orElseThrow();
        checkEquality(warehouse, actual3);
    }

    @Test
    void getRpTest() throws SQLException {
        UUID companyId = getCompany(true).id();
        List<UUID> userIds = getUsers(2, true).stream().map(User::id).toList();
        CreateWarehouse createWarehouse = generateWarehouse(userIds);

        Warehouse warehouse = rpWarehouse.add(createWarehouse, companyId).orElseThrow();

        assertTrue(inMemoryRpWarehouse.get(warehouse.id()).isEmpty());

        Warehouse actual = warehouseService.get(warehouse.id(), companyId);
        checkEquality(warehouse, actual);

        assertTrue(inMemoryRpWarehouse.get(warehouse.id()).isPresent());

        assertThrows(DataNotFoundException.class, () -> warehouseService.get(UUID.randomUUID(), companyId));
    }

    private void checkEquality(CreateWarehouse createWarehouse, Warehouse warehouse) {
        assertEquals(createWarehouse.name(), warehouse.name());
        assertEquals(createWarehouse.location().getAddress(), warehouse.location().getAddress());
        assertEquals(createWarehouse.stockLevel(), warehouse.stockLevel());
        assertEquals(createWarehouse.capacity(), warehouse.capacity());
        assertEquals(createWarehouse.admins().stream().sorted().toList(), warehouse.admins().stream().sorted().toList());
    }

    private void checkEquality(Warehouse expected, Warehouse actual) {
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.location().getAddress(), actual.location().getAddress());
        assertEquals(expected.stockLevel(), actual.stockLevel());
        assertEquals(expected.capacity(), actual.capacity());
        assertEquals(expected.admins().stream().sorted().toList(), actual.admins().stream().sorted().toList());
        assertEquals(expected.companyId(), actual.companyId());
        assertEquals(expected.createdAt(), actual.createdAt());
        assertEquals(expected.updatedAt(), actual.updatedAt());
    }

}
