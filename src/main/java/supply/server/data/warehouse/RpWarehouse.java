package supply.server.data.warehouse;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.utils.Address;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
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

        for (UUID admin: createWarehouse.admins()) {
            jdbcSession
                    .sql("""
                            INSERT INTO warehouse_admins
                            (user_id, warehouse_id)
                            VALUES (?, ?)
                            """)
                    .set(admin)
                    .set(warehouseId)
                    .insert(Outcome.VOID);
        }

        jdbcSession
                .sql("""
                        INSERT INTO company_warehouses
                        (warehouse, company)
                        VALUES (?, ?)
                        """)
                .set(warehouseId)
                .set(createWarehouse.companyId())
                .insert(Outcome.VOID);

        return Optional.of(new Warehouse(
                warehouseId,
                createWarehouse.name(),
                createWarehouse.location(),
                createWarehouse.stockLevel(),
                createWarehouse.capacity(),
                createWarehouse.admins(),
                createWarehouse.companyId(),
                LocalDate.now(),
                LocalDate.now(),
                dataSource
        ));

    }

    public Optional<Warehouse> get(UUID id) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT w.id, w.name, w.location, w.stock_level,
                               w.capacity, w.created_at, w.updated_at,
                               ARRAY_AGG(DISTINCT wa.user_id) AS admins,
                               cw.company AS company_id
                        FROM warehouse w
                        LEFT JOIN warehouse_admins wa ON w.id = wa.warehouse_id
                        LEFT JOIN company_warehouses cw ON w.id = cw.warehouse
                        WHERE w.id = ?
                        GROUP BY w.id, cw.company
                        """)
                .set(id)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(compactWarehouseFromResultSet(rset));
                    }
                    return Optional.empty();
                });
    }

    public PaginatedList<Warehouse> getAll(String prefix, UUID companyId, Pagination pagination) throws SQLException {
        String SQLWith = """
                WITH params AS (SELECT ? AS lower_prefix),
                     warehouse_table AS (
                         SELECT
                             w.id,
                             w.name,
                             w.location,
                             w.stock_level,
                             w.capacity,
                             w.created_at,
                             w.updated_at,
                             ARRAY_AGG(DISTINCT wa.user_id) AS admins,
                             cw.company AS company_id,
                             CASE
                                 WHEN lower(w.name) LIKE concat(lower_prefix, '%')
                                     THEN 1
                                 ELSE 2
                             END AS priority
                         FROM warehouse w
                         LEFT JOIN warehouse_admins wa ON w.id = wa.warehouse_id
                         LEFT JOIN company_warehouses cw ON w.id = cw.warehouse
                         JOIN params ON true
                         WHERE cw.company = ?
                         GROUP BY w.id, cw.company, lower_prefix
                     )
                SELECT *,
                       (SELECT COUNT(*) FROM warehouse_table WHERE priority = 1) AS total_count
                FROM warehouse_table
                WHERE priority = 1
                ORDER BY priority ASC, created_at DESC
                LIMIT ?
                OFFSET ?;
            """;

        return new JdbcSession(dataSource)
                .sql(SQLWith)
                .set(prefix.toLowerCase() + "%")
                .set(companyId)
                .set(pagination.limit())
                .set(pagination.offset())
                .select((rset, stmt) -> {
                    List<Warehouse> warehouses = new ArrayList<>();
                    long total = 0;

                    while (rset.next()) {
                        if (total == 0) {
                            total = rset.getLong("total_count");
                        }
                        Warehouse warehouse = compactWarehouseFromResultSet(rset);
                        warehouses.add(warehouse);
                    }

                    return new PaginatedList<>(total, warehouses);
                });
    }


    private Warehouse compactWarehouseFromResultSet(ResultSet rset) throws SQLException {
        Array adminsArray = rset.getArray("admins");
        List<UUID> admins = new ArrayList<>();
        if (adminsArray != null) {
            UUID[] adminIds = (UUID[]) adminsArray.getArray();
            admins.addAll(Arrays.asList(adminIds));
        }

        return new Warehouse(
                UUID.fromString(rset.getString("id")),
                rset.getString("name"),
                new Address(rset.getString("location")),
                rset.getLong("stock_level"),
                rset.getLong("capacity"),
                admins,
                UUID.fromString(rset.getString("company_id")),
                rset.getDate("created_at").toLocalDate(),
                rset.getDate("updated_at").toLocalDate(),
                dataSource
        );
    }

}
