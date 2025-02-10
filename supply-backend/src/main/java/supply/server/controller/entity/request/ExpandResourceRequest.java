package supply.server.controller.entity.request;

import jakarta.validation.constraints.NotNull;

public record ExpandResourceRequest(
    @NotNull
    String id,
    @NotNull
    int quantity
) {
}
