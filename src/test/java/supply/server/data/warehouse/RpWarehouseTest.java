package supply.server.data.warehouse;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Test;
import supply.server.configuration.DBConnection;
import supply.server.data.utils.Address;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RpWarehouseTest extends DBConnection {

    private final DataSource dataSource = dataSource();

    @Test
    void addTest() throws SQLException {
        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);

        CreateWarehouse createWarehouse = new CreateWarehouse(
                "test",
                new Address("test"),
                0L,
                0L,
                List.of(UUID.randomUUID()),
                UUID.randomUUID()
        );

        Warehouse warehouse = rpWarehouse.add(createWarehouse).orElseThrow();

        assertEquals(createWarehouse.name(), warehouse.name());
        assertEquals(createWarehouse.location().getAddress(), warehouse.location().getAddress());
        assertEquals(createWarehouse.stockLevel(), warehouse.stockLevel());
        assertEquals(createWarehouse.capacity(), warehouse.capacity());

        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Warehouse result = jdbcSession
                .sql("""
                        SELECT id, name, location, stock_level,
                         capacity, created_at, updated_at
                        FROM warehouse
                        WHERE id = ?
                        """)
                .set(warehouse.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(new Warehouse(
                                UUID.fromString(rset.getString("id")),
                                rset.getString("name"),
                                new Address(rset.getString("location")),
                                rset.getLong("stock_level"),
                                rset.getLong("capacity"),
                                List.of(),
                                null,
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate(),
                                dataSource
                        ));
                    }
                    return Optional.<Warehouse>empty();
                }).orElseThrow();

        assertEquals(warehouse.id(), result.id());
        assertEquals(warehouse.name(), result.name());
        assertEquals(warehouse.location().getAddress(), result.location().getAddress());
        assertEquals(warehouse.stockLevel(), result.stockLevel());
        assertEquals(warehouse.capacity(), result.capacity());
        assertEquals(warehouse.admins(), result.admins());
        assertEquals(warehouse.companyId(), result.companyId());
        assertEquals(warehouse.createdAt(), result.createdAt());
        assertEquals(warehouse.updatedAt(), result.updatedAt());
    }

}
