package supply.server.controller.entity.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import supply.server.data.warehouse.CreateWarehouse;

import java.util.List;
import java.util.UUID;

public record CreateWarehouseRequest(
        @NotNull
        String name,
        String location,
        @NotNull
        Long stockLevel,
        @NotNull
        Long capacity,
        @NotNull
        @NotEmpty
        List<String> admins
) {
    public CreateWarehouse toCreateWarehouse() {
        return new CreateWarehouse(
                name,
                location,
                stockLevel,
                capacity,
                admins.stream().map(UUID::fromString).toList()
        );
    }
}
