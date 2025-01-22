package supply.server.data.project;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record Project(
    UUID id,
    String name,
    String description,
    UUID companyId,
    LocalDate createdAt,
    LocalDate updatedAt
) implements Serializable {
}
