package supply.server.data.resource;

import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record EditResource(
        UUID resourceId,
        int quantity,
        Optional<List<URL>> images,
        Optional<String> name,
        Optional<Integer> count,
        Optional<Unit> unit,
        Optional<ResourceType> type,
        Optional<UUID> projectId,
        Optional<ResourceStatus> status,
        Optional<String> description,
        Optional<UUID> userId,
        Optional<UUID> warehouseId
) {
}
