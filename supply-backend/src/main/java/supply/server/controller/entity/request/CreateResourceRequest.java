package supply.server.controller.entity.request;

import supply.server.data.resource.CreateResource;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import java.net.URL;
import java.util.List;
import java.util.UUID;

public record CreateResourceRequest(
        List<String> images,
        String name,
        int count,
        Unit unit,
        ResourceType type,
        String userId,
        String warehouseId,
        String projectId,
        ResourceStatus status,
        String description
) {
    public CreateResource toCreateResource() {
        return new CreateResource(
                images,
                name,
                count,
                unit.toString(),
                type.toString(),
                UUID.fromString(userId),
                UUID.fromString(warehouseId),
                UUID.fromString(projectId),
                status.toString(),
                description
        );
    }
}
