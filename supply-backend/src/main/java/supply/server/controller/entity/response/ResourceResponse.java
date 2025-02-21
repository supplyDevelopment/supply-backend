package supply.server.controller.entity.response;

import supply.server.data.resource.Resource;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ResourceResponse(
        UUID id,
        List<URL> images,
        String name,
        int count,
        Unit unit,
        ResourceType type,
        UUID projectId,
        ResourceStatus status,
        String description,

        UUID warehouseId,
        UUID userId,
        LocalDate createdAt,
        LocalDate updatedAt
) {
    public ResourceResponse(Resource resource) {
        this(
                resource.id(),
                resource.images(),
                resource.name(),
                resource.count(),
                resource.unit(),
                resource.type(),
                resource.projectId(),
                resource.status(),
                resource.description(),
                resource.warehouseId(),
                resource.userId(),
                resource.createdAt(),
                resource.updatedAt()
        );
    }
}
