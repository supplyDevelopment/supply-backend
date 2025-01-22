package supply.server.service.repository;

import lombok.AllArgsConstructor;
import supply.server.configuration.exception.DataNotFoundException;
import supply.server.configuration.exception.DbException;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.Redis;
import supply.server.data.warehouse.CreateWarehouse;
import supply.server.data.warehouse.RpWarehouse;
import supply.server.data.warehouse.Warehouse;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class WarehouseRepositoryService {

    private final RpWarehouse rpWarehouse;

    private final Redis<Warehouse> inMemoryRpWarehouse;

    public Warehouse add(CreateWarehouse createWarehouse, UUID companyId) {
        Warehouse warehouse;
        try {
            Optional<Warehouse> warehouseOpt = rpWarehouse.add(createWarehouse, companyId);

            if (warehouseOpt.isPresent()) {
                warehouse = warehouseOpt.get();
                inMemoryRpWarehouse.set(warehouse.id(), warehouse);
            } else {
                throw new DbException("Failed to add warehouse");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return warehouse;
    }

    public Warehouse get(UUID warehouseId, UUID companyId) {
        Warehouse warehouse;
        try {
            Optional<Warehouse> warehouseOpt = inMemoryRpWarehouse.get(warehouseId);
            if (warehouseOpt.isEmpty()) {
                warehouseOpt = rpWarehouse.get(warehouseId, companyId);
                if (warehouseOpt.isPresent()) {
                    warehouse = warehouseOpt.get();
                    inMemoryRpWarehouse.set(warehouse.id(), warehouse);
                } else {
                    throw new DataNotFoundException("Warehouse with id " + warehouseId + " not found");
                }
            } else {
                if (!warehouseOpt.get().companyId().equals(companyId)) {
                    throw new DataNotFoundException("Warehouse with id " + warehouseId + " not found");
                }
            }
            warehouse = warehouseOpt.get();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return warehouse;
    }

    public PaginatedList<Warehouse> getAll(String prefix, UUID companyId, Pagination pagination) {
        PaginatedList<Warehouse> warehouses;
        try {
            warehouses = rpWarehouse.getAll(prefix, companyId, pagination);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return warehouses;
    }

}
