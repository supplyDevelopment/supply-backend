package supply.server.controller.entity.request;

import jakarta.validation.constraints.NotNull;
import supply.server.data.resource.EditResource;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record EditResourceRequest(
        @NotNull
        int quantity,
        String name,
        String type,
        String projectId,
        String status,
        String description,
        String userId,
        String warehouseId
) {
    public EditResource toEditResource() {
        return new EditResource(
                quantity,
                Objects.isNull(name) ? Optional.empty() : Optional.of(name),
                Objects.isNull(type) ? Optional.empty() : Optional.of(ResourceType.fromString(type)),
                Objects.isNull(projectId) ? Optional.empty() : Optional.of(UUID.fromString(projectId)),
                Objects.isNull(status) ? Optional.empty() : Optional.of(ResourceStatus.fromString(status)),
                Objects.isNull(description) ? Optional.empty() : Optional.of(description),
                Objects.isNull(userId) ? Optional.empty() : Optional.of(UUID.fromString(userId)),
                Objects.isNull(warehouseId) ? Optional.empty() : Optional.of(UUID.fromString(warehouseId))
        );
    }
}
