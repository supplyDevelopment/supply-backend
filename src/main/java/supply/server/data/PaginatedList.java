package supply.server.data;

import java.util.List;

public record PaginatedList<T>(
        int total,
        List<T> items
) {
}
