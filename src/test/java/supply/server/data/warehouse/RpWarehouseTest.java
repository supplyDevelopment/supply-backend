package supply.server.data.warehouse;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Test;
import supply.server.configuration.DataCreator;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.company.Company;
import supply.server.data.user.User;
import supply.server.data.utils.Address;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RpWarehouseTest extends DataCreator {

    private final DataSource dataSource = dataSource();

    @Test
    void addTest() throws SQLException {
        List<UUID> users = getUsers(2, true).stream().map(User::id).toList();

        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);

        CreateWarehouse createWarehouse = generateWarehouse(users);

        Warehouse warehouse = rpWarehouse.add(createWarehouse, getCompany(false).id()).orElseThrow();

        assertEquals(createWarehouse.name(), warehouse.name());
        assertEquals(createWarehouse.location().getAddress(), warehouse.location().getAddress());
        assertEquals(createWarehouse.stockLevel(), warehouse.stockLevel());
        assertEquals(createWarehouse.capacity(), warehouse.capacity());

        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Warehouse result = jdbcSession
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
                .set(warehouse.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        Array adminsArray = rset.getArray("admins");
                        List<UUID> admins = new ArrayList<>();
                        if (adminsArray != null) {
                            UUID[] adminIds = (UUID[]) adminsArray.getArray();
                            admins.addAll(Arrays.asList(adminIds));
                        }

                        return Optional.of(new Warehouse(
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
                        ));
                    }
                    return Optional.<Warehouse>empty();
                }).orElseThrow();

        assertEquals(warehouse.id(), result.id());
        assertEquals(warehouse.name(), result.name());
        assertEquals(warehouse.location().getAddress(), result.location().getAddress());
        assertEquals(warehouse.stockLevel(), result.stockLevel());
        assertEquals(warehouse.capacity(), result.capacity());
        assertEquals(warehouse.admins().stream().sorted().toList(), result.admins().stream().sorted().toList());
        assertEquals(warehouse.companyId(), result.companyId());
        assertEquals(warehouse.createdAt(), result.createdAt());
        assertEquals(warehouse.updatedAt(), result.updatedAt());
    }

    @Test
    void getTest() throws SQLException {
        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);

        Warehouse expected = getWarehouse(false);
        Warehouse actual = rpWarehouse.get(expected.id(), expected.companyId()).orElseThrow();

        assertEquals(expected.id(), actual.id());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.location().getAddress(), actual.location().getAddress());
        assertEquals(expected.stockLevel(), actual.stockLevel());
        assertEquals(expected.capacity(), actual.capacity());
        assertEquals(expected.admins().stream().sorted().toList(), actual.admins().stream().sorted().toList());
        assertEquals(expected.companyId(), actual.companyId());
        assertEquals(expected.createdAt(), actual.createdAt());
        assertEquals(expected.updatedAt(), actual.updatedAt());

        assertTrue(rpWarehouse.get(expected.id(), UUID.randomUUID()).isEmpty());
    }

    @Test
    void getAllTest() throws SQLException {
        List<UUID> companyIds = getCompanies(2, true).stream().map(Company::id).toList();

        UUID userId = getUser(true).id();

        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);
        List<Warehouse> expected = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            if (i % 4 == 0) {
                expected.add(rpWarehouse.add(new CreateWarehouse(
                        "adding" + i,
                        new Address("test" + i),
                        0L,
                        0L,
                        List.of(userId)
                ), companyIds.get(0)).orElseThrow());
            } else if (i % 2 == 0) {
                rpWarehouse.add(new CreateWarehouse(
                        "getting" + i,
                        new Address("test" + i),
                        0L,
                        0L,
                        List.of(userId)
                ), companyIds.get(1)).orElseThrow();
            } else {
                rpWarehouse.add(new CreateWarehouse(
                        "agetting" + i,
                        new Address("test" + i),
                        0L,
                        0L,
                        List.of(userId)
                ), companyIds.get(0)).orElseThrow();
            }
        }

        PaginatedList<Warehouse> actual = rpWarehouse.getAll("adding", companyIds.get(0), new Pagination(20, 0));
        assertEquals(4, actual.total());
        for (Warehouse expectedWarehouse : expected) {
            for (Warehouse actualWarehouse : actual.items()) {
                if (expectedWarehouse.id().equals(actualWarehouse.id())) {
                    assertEquals(expectedWarehouse.id(), actualWarehouse.id());
                    assertEquals(expectedWarehouse.name(), actualWarehouse.name());
                    assertEquals(expectedWarehouse.location().getAddress(), actualWarehouse.location().getAddress());
                    assertEquals(expectedWarehouse.stockLevel(), actualWarehouse.stockLevel());
                    assertEquals(expectedWarehouse.capacity(), actualWarehouse.capacity());
                    assertEquals(expectedWarehouse.admins(), actualWarehouse.admins());
                    assertEquals(expectedWarehouse.admins().get(0), actualWarehouse.admins().get(0));
                    assertEquals(expectedWarehouse.companyId(), actualWarehouse.companyId());
                    assertEquals(expectedWarehouse.createdAt(), actualWarehouse.createdAt());
                    assertEquals(expectedWarehouse.updatedAt(), actualWarehouse.updatedAt());
                }
            }
        }

        assertEquals(12, rpWarehouse.getAll("a", companyIds.get(0), new Pagination(20, 0)).total());
        assertEquals(12, rpWarehouse.getAll("", companyIds.get(0), new Pagination(20, 0)).total());
    }

}
