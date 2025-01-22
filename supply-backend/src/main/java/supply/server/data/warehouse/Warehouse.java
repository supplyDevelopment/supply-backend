package supply.server.data.warehouse;

import supply.server.data.utils.Address;

import javax.sql.DataSource;
import java.io.Serializable;
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
) implements Serializable {
}
