package supply.server.data.resource.history;

import org.apache.commons.lang3.tuple.Pair;
import supply.server.data.resource.types.ResourceStatus;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
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
    public Map<String, Pair<String, String>> toMap() {
        Map<String, Pair<String, String>> map = new LinkedHashMap<>();
        if (prev_name != null && goal_name != null) {
            map.put("name", Pair.of(prev_name, goal_name));
        }
        if (prev_status != null && goal_status != null) {
            map.put("status", Pair.of(prev_status.name(), goal_status.name()));
        }
        if (prev_count != null && goal_count != null) {
            map.put("count", Pair.of(String.valueOf(prev_count), String.valueOf(goal_count)));
        }
        if (prev_projectId != null && goal_projectId != null) {
            map.put("projectId", Pair.of(prev_projectId.toString(), goal_projectId.toString()));
        }
        if (prev_description != null && goal_description != null) {
            map.put("description", Pair.of(prev_description, goal_description));
        }
        if (prev_warehouseId != null && goal_warehouseId != null) {
            map.put("warehouseId", Pair.of(prev_warehouseId.toString(), goal_warehouseId.toString()));
        }
        if (prev_userId != null && goal_userId != null) {
            map.put("userId", Pair.of(prev_userId.toString(), goal_userId.toString()));
        }

        return map;
    }
}
