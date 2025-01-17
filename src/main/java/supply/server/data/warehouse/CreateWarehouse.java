package supply.server.data.warehouse;

import supply.server.data.utils.Address;

import java.util.List;
import java.util.UUID;

public record CreateWarehouse(
        String name,
        Address location,
        Long stockLevel,
        Long capacity,

        List<UUID> admins
) {
    public CreateWarehouse(
            String name,
            String location,
            Long stockLevel,
            Long capacity,
            List<UUID> admins
    ) {
        this(
                name,
                new Address(location),
                stockLevel,
                capacity,
                admins
        );
    }
}
