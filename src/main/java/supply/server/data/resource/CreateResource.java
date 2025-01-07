package supply.server.data.resource;

import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import java.net.URL;
import java.util.List;
import java.util.UUID;

public record CreateResource(
    List<URL> images,
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
