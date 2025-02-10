package supply.server.controller.entity.response;

import supply.server.data.resource.Resource;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import java.net.URL;
import java.util.List;
import java.util.UUID;

public record ResourcePartialInfoResponse(
        UUID id,
        List<URL> images,
        String name,
        int count,
        Unit unit,
        ResourceType type,
        ResourceStatus status
) {
    public ResourcePartialInfoResponse(Resource resource) {
        this(
                resource.id(),
                resource.images(),
                resource.name(),
                resource.count(),
                resource.unit(),
                resource.type(),
                resource.status()
        );
    }
}
