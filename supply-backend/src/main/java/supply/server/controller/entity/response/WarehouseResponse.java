package supply.server.controller.entity.response;

import supply.server.data.utils.Address;
import supply.server.data.warehouse.Warehouse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record WarehouseResponse(
        UUID id,
        String name,
        Address location,
        Long stockLevel,
        Long capacity,

        List<UUID> admins,
        UUID companyId,
        LocalDate createdAt,
        LocalDate updatedAt
) {
    public WarehouseResponse(Warehouse warehouse) {
        this(
                warehouse.id(),
                warehouse.name(),
                warehouse.location(),
                warehouse.stockLevel(),
                warehouse.capacity(),
                warehouse.admins(),
                warehouse.companyId(),
                warehouse.createdAt(),
                warehouse.updatedAt()
        );
    }
}
