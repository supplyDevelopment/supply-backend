package supply.server.controller.entity.request;

import jakarta.validation.constraints.NotNull;
import supply.server.data.Pagination;

public record PaginationRequest(
        @NotNull
        int page,
        @NotNull
        int size
) {
    public Pagination toPagination() {
        return new Pagination(page, size);
    }
}
