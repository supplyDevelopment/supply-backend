package supply.server.controller.entity.response;

import supply.server.data.warehouse.Warehouse;

import java.util.UUID;

public record WarehousePartialInfoResponse(
        UUID id,
        String name,
        Long stockLevel,
        Long capacity
) {
    public WarehousePartialInfoResponse(Warehouse warehouse) {
        this(
                warehouse.id(),
                warehouse.name(),
                warehouse.stockLevel(),
                warehouse.capacity()
        );
    }
}
