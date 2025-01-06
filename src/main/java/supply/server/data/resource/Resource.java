package supply.server.data.resource;

import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.utils.Unit;

import javax.sql.DataSource;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Resource(
        UUID id,
        List<URL> images,
        String name,
        int count,
        Unit unit,
        ResourceType type,
        UUID projectId,
        ResourceStatus status,
        String description,

        UUID warehouseId,
        UUID userId,
        LocalDate createdAt,
        LocalDate updatedAt,
        DataSource dataSource
) {
}
