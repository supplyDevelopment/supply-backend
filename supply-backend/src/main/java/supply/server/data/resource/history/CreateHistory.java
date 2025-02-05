package supply.server.data.resource.history;

import supply.server.data.resource.EditResource;
import supply.server.data.resource.Resource;
import supply.server.data.resource.types.ResourceStatus;

import java.util.UUID;

public record CreateHistory(
        int quantity,
        UUID prev_id,
        UUID goal_id,
        String prev_name,
        String goal_name,
        ResourceStatus prev_status,
        ResourceStatus goal_status,
        Integer prev_count,
        Integer goal_count,
        UUID prev_projectId,
        UUID goal_projectId,
        String prev_description,
        String goal_description,
        UUID prev_warehouseId,
        UUID goal_warehouseId,
        UUID prev_userId,
        UUID goal_userId
) {
    public CreateHistory(EditResource editResource, Resource resource, Resource goalResource) {
        this(
                editResource.quantity(),
                resource.id(),
                goalResource.id(),
                editResource.name().isEmpty() ? null : resource.name(),
                editResource.name().isEmpty() ? null : goalResource.name(),
                editResource.status().isEmpty() ? null : resource.status(),
                editResource.status().isEmpty() ? null : goalResource.status(),
                editResource.count().isEmpty() ? null : resource.count(),
                editResource.count().isEmpty() ? null : goalResource.count(),
                editResource.projectId().isEmpty() ? null : resource.projectId(),
                editResource.projectId().isEmpty() ? null : goalResource.projectId(),
                editResource.description().isEmpty() ? null : resource.description(),
                editResource.description().isEmpty() ? null : goalResource.description(),
                editResource.warehouseId().isEmpty() ? null : resource.warehouseId(),
                editResource.warehouseId().isEmpty() ? null : goalResource.warehouseId(),
                editResource.userId().isEmpty() ? null : resource.userId(),
                editResource.userId().isEmpty() ? null : goalResource.userId()
        );
    }
}
