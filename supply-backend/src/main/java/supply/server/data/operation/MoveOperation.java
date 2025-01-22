package supply.server.data.operation;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record MoveOperation(
    UUID id,
    Integer quantity,
    UUID from,
    UUID to,
    UUID resourceId,
    UUID companyId,
    LocalDate createdAt,
    Boolean applied,
    LocalDate appliedAt
) implements Serializable {
}
