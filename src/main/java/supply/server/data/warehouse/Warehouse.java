package supply.server.data.warehouse;

import org.apache.commons.lang3.NotImplementedException;
import supply.server.data.resource.Resource;
import supply.server.data.utils.Address;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Warehouse (
    UUID id,
    String name,
    Address location,
    Long stockLevel,
    Long capacity,

    List<UUID> admins,
    UUID companyId,
    LocalDate createdAt,
    LocalDate updatedAt,
    DataSource dataSource
) {
}
