package supply.server.data;

public record Pagination(
        int limit,
        int offset
) {
}
