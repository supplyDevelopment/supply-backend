package supply.server.data.resource;

import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import java.util.UUID;

public record CreateResource(
    String name,
    int count,
    Unit unit,
    ResourceType type,
    UUID userId,
    UUID warehouseId,
    UUID projectId,
    ResourceStatus status,
    String description
) {
}
