package supply.server.data;

public record Pagination(
        int limit,
        int offset
) {
    @Override
    public int limit() {
        return limit * (offset + 1);
    }

    @Override
    public int offset() {
        return limit * offset;
    }
}
