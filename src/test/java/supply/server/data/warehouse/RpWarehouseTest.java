package supply.server.data.warehouse;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Test;
import supply.server.configuration.DBConnection;
import supply.server.data.company.Company;
import supply.server.data.company.CreateCompany;
import supply.server.data.company.RpCompany;
import supply.server.data.user.CreateUser;
import supply.server.data.user.RpUser;
import supply.server.data.user.User;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;

import javax.sql.DataSource;
import java.security.SecureRandom;
import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RpWarehouseTest extends DBConnection {

    private final DataSource dataSource = dataSource();

    @Test
    void addTest() throws SQLException {
        RpUser rpUser = new RpUser(dataSource);
        User user1 = rpUser.add(new CreateUser(
                new UserName("testFirstName", "testSecondName", "testLastName"),
                new Email("example@example.com"),
                new Phone("+71234567890"),
                "testPassword",
                UUID.randomUUID(),
                List.of(UserPermission.DELETE)
        )).orElseThrow();

        User user2 = rpUser.add(new CreateUser(
                new UserName("testFirstName", "testSecondName", "testLastName"),
                new Email("example0@example0.com"),
                new Phone("+71234567890"),
                "testPassword",
                UUID.randomUUID(),
                List.of(UserPermission.DELETE)
        )).orElseThrow();

        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);

        CreateWarehouse createWarehouse = new CreateWarehouse(
                "test",
                new Address("test"),
                0L,
                0L,
                List.of(user1.id(), user2.id()),
                getCompanyId()
        );

        Warehouse warehouse = rpWarehouse.add(createWarehouse).orElseThrow();

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
        assertEquals(warehouse.admins(), result.admins());
        assertEquals(warehouse.companyId(), result.companyId());
        assertEquals(warehouse.createdAt(), result.createdAt());
        assertEquals(warehouse.updatedAt(), result.updatedAt());
    }

    @Test
    void getTest() throws SQLException {
        RpUser rpUser = new RpUser(dataSource);
        User user = rpUser.add(new CreateUser(
                new UserName("testFirstName", "testSecondName", "testLastName"),
                new Email("example1@example1.com"),
                new Phone("+71234567890"),
                "testPassword",
                UUID.randomUUID(),
                List.of(UserPermission.DELETE)
        )).orElseThrow();


        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);

        CreateWarehouse createWarehouse = new CreateWarehouse(
                "test",
                new Address("test"),
                0L,
                0L,
                List.of(user.id()),
                getCompanyId()
        );

        Warehouse expected = rpWarehouse.add(createWarehouse).orElseThrow();
        Warehouse actual = rpWarehouse.getById(expected.id()).orElseThrow();

        assertEquals(expected.id(), actual.id());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.location().getAddress(), actual.location().getAddress());
        assertEquals(expected.stockLevel(), actual.stockLevel());
        assertEquals(expected.capacity(), actual.capacity());
        assertEquals(expected.admins(), actual.admins());
        assertEquals(expected.admins().get(0), actual.admins().get(0));
        assertEquals(expected.companyId(), actual.companyId());
        assertEquals(expected.createdAt(), actual.createdAt());
        assertEquals(expected.updatedAt(), actual.updatedAt());
    }

}
