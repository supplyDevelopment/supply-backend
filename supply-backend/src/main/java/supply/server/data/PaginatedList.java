package supply.server.data;

import java.util.List;

public record PaginatedList<T>(
        long total,
        List<T> items
) {
}
