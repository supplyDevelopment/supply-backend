package supply.server.data.resource.history;

import supply.server.data.resource.types.ResourceStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ResourceHistory(
        UUID id,
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
        UUID goal_userId,
        LocalDate createdAt
) {
}
