package supply.server.controller.entity.response;

import org.apache.commons.lang3.tuple.Pair;
import supply.server.data.resource.history.ResourceHistory;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record ResourceHistoryResponse(
        UUID id,
        int quantity,
        Map<String, Pair<String, String>> changes,
        LocalDate createdAt
) {
    public ResourceHistoryResponse(ResourceHistory resourceHistory) {
        this(
                resourceHistory.id(),
                resourceHistory.quantity(),
                resourceHistory.toMap(),
                resourceHistory.createdAt()
        );
    }
}
