package supply.server.data.resource;

import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record EditResource(
        int quantity,
        Optional<String> name,
        Optional<ResourceType> type,
        Optional<UUID> projectId,
        Optional<ResourceStatus> status,
        Optional<String> description,
        Optional<UUID> userId,
        Optional<UUID> warehouseId
) {
    public CreateResource edit(Resource resource) {
        return new CreateResource(
                resource.images(),
                name.orElse(resource.name()),
                quantity,
                resource.unit(),
                type.orElse(resource.type()),
                userId.orElse(resource.userId()),
                warehouseId.orElse(resource.warehouseId()),
                projectId.orElse(resource.projectId()),
                status.orElse(resource.status()),
                description.orElse(resource.description())
        );
    }
}
