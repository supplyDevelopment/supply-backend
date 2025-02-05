package supply.server.controller.entity.request;

import jakarta.validation.constraints.NotNull;

public record CreateProjectRequest(
    @NotNull
    String name,
    @NotNull
    String description
) {
}
