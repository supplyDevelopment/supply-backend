package supply.server.data.project;

import java.util.UUID;

public record Project(
    UUID id,
    String name,
    String description
) {
}
