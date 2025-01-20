package supply.server.data.operation;

import java.time.LocalDate;
import java.util.UUID;

public record CreateMoveOperation(
    Integer quantity,
    UUID from,
    UUID to,
    UUID resourceId,
    UUID companyId
) {
}
