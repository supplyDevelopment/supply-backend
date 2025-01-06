package supply.server.data.resource;

import java.util.Optional;
import java.util.UUID;

public record ResourceFilters(
        Optional<UUID> warehouseId
) {
}
// TODO: implement all possible filters