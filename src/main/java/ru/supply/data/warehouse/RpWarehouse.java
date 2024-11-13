package ru.supply.data.warehouse;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import ru.supply.data.utils.Address;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class RpWarehouse {

    public final DataSource dataSource;

    public Optional<Warehouse> add(Warehouse warehouse) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID warehouseId = jdbcSession
                .sql("""
                        INSERT INTO warehouse (name, location, company_id, stock_level, capacity, created_at, updated_at)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """)
                .set(warehouse.name())
                .set(warehouse.location().getAddress())
                .set(warehouse.companyId())
                .set(warehouse.stockLevel())
                .set(warehouse.capacity())
                .set(LocalDate.now())
                .set(LocalDate.now())
                .insert(new SingleOutcome<>(UUID.class));

        return Optional.of(new Warehouse(
                warehouseId,
                warehouse.name(),
                warehouse.location(),
                warehouse.companyId(),
                warehouse.stockLevel(),
                warehouse.capacity(),
                LocalDate.now(),
                LocalDate.now()
        ));

    }

    public Optional<Warehouse> getById(UUID id) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT *
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

    private static Warehouse compactWarehouseFromResultSet(ResultSet rset) throws SQLException {
        return new Warehouse(
                UUID.fromString(rset.getString("id")),
                rset.getString("name"),
                new Address(rset.getString("location")),
                UUID.fromString(rset.getString("company_id")),
                rset.getLong("stock_level"),
                rset.getLong("capacity"),
                rset.getDate("created_at").toLocalDate(),
                rset.getDate("updated_at").toLocalDate()
        );
    }

}
