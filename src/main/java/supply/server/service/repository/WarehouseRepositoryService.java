package supply.server.service.repository;

import lombok.AllArgsConstructor;
import supply.server.configuration.exception.DataNotFound;
import supply.server.configuration.exception.DbException;
import supply.server.data.warehouse.CreateWarehouse;
import supply.server.data.warehouse.RpWarehouse;
import supply.server.data.warehouse.Warehouse;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class WarehouseRepositoryService {

    private final RpWarehouse rpWarehouse;

    public Warehouse add(CreateWarehouse createWarehouse) {
        Warehouse warehouse;
        try {
            Optional<Warehouse> warehouseOpt = rpWarehouse.add(createWarehouse);

            if (warehouseOpt.isPresent()) {
                warehouse = warehouseOpt.get();
            } else {
                throw new DbException("Failed to add warehouse");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return warehouse;
    }

    public Warehouse get(UUID warehouseId) {
        Warehouse warehouse;
        try {
            Optional<Warehouse> warehouseOpt = rpWarehouse.get(warehouseId);

            if (warehouseOpt.isPresent()) {
                warehouse = warehouseOpt.get();
            } else {
                throw new DataNotFound("Warehouse with id " + warehouseId + " not found");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return warehouse;
    }

}
