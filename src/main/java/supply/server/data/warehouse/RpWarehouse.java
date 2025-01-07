package supply.server.data.warehouse;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import supply.server.data.utils.Address;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class RpWarehouse {

    public final DataSource dataSource;

    public Optional<Warehouse> add(CreateWarehouse createWarehouse) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID warehouseId = jdbcSession
                .sql("""
                        INSERT INTO warehouse
                        (name, location, stock_level,
                         capacity, created_at)
                        VALUES (?, ?, ?, ?, ?)
                        """)
                .set(createWarehouse.name())
                .set(createWarehouse.location().getAddress())
                .set(createWarehouse.stockLevel())
                .set(createWarehouse.capacity())
                .set(LocalDate.now())
                .insert(new SingleOutcome<>(UUID.class));

        return Optional.of(new Warehouse(
                warehouseId,
                createWarehouse.name(),
                createWarehouse.location(),
                createWarehouse.stockLevel(),
                createWarehouse.capacity(),
                List.of(), // TODO: implement connection with admins
                null, // TODO: implement connection with company
                LocalDate.now(),
                LocalDate.now(),
                dataSource
        ));

    }

    public Optional<Warehouse> getById(UUID id) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT id, name, location, stock_level,
                         capacity, created_at, updated_at
                        FROM warehouse
                        WHERE id = ?
                        """)
                .set(id)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(compactWarehouseFromResultSet(rset));
                    }
                    return Optional.empty();
                });

    }

    private Warehouse compactWarehouseFromResultSet(ResultSet rset) throws SQLException {
        return new Warehouse(
                UUID.fromString(rset.getString("id")),
                rset.getString("name"),
                new Address(rset.getString("location")),
                rset.getLong("stock_level"),
                rset.getLong("capacity"),
                List.of(), // TODO: implement connection with admins
                null, // TODO: implement connection with company
                rset.getDate("created_at").toLocalDate(),
                rset.getDate("updated_at").toLocalDate(),
                dataSource
        );
    }

}
