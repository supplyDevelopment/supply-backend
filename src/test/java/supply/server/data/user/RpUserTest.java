package supply.server.data.user;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Test;
import supply.server.configuration.DBConnection;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static supply.server.data.user.RpUser.compactUserFromResultSet;

public class RpUserTest extends DBConnection {

    private final DataSource dataSource = dataSource();

    @Test
    void addTest() throws SQLException {
        RpUser rpUser = new RpUser(dataSource);
        CreateUser createUser = new CreateUser(
                new UserName("testFirstName", "testSecondName", "testLastName"),
                new Email("example@example.com"),
                new Phone("+71234567890"),
                "testPassword",
                UUID.randomUUID(),
                List.of(UserPermission.DELETE)
        );

        User user = rpUser.add(createUser).orElseThrow();

        assertEquals(createUser.name().getFirstName(), user.name().getFirstName());
        assertEquals(createUser.name().getSecondName(), user.name().getSecondName());
        assertEquals(createUser.name().getLastName().orElse(null), user.name().getLastName().orElse(null));
        assertEquals(createUser.email().getEmail(), user.email().getEmail());
        assertEquals(createUser.phone().getPhone(), user.phone().getPhone());
        assertEquals(createUser.password(), user.password());
        assertEquals(createUser.permissions().get(0), user.permissions().get(0));


        JdbcSession jdbcSession = new JdbcSession(dataSource);
        User userFromDB = jdbcSession.sql("""
                SELECT u.id,
                           (u.name).first_name as firstName,
                           (u.name).second_name as secondName,
                           (u.name).last_name as lastName,
                           u.password,
                           u.privileges,
                           u.email,
                           u.phone,
                           u.created_at,
                           u.updated_at
                    FROM company_user u
                    WHERE u.id = ?
                """)
                .set(user.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        Email userEmail = new Email(rset.getString("email"));
                        UserName userName = new UserName(
                                rset.getString("firstName"),
                                rset.getString("secondName"),
                                rset.getString("lastName")
                        );
                        Phone userPhone = new Phone(rset.getString("phone"));
                        List<UserPermission> userPermissions = Arrays.stream(
                                (String[]) rset.getArray("privileges").getArray()
                        ).map(UserPermission::valueOf).toList();

                        return Optional.of(new User(
                                UUID.fromString(rset.getString("id")),
                                userName,
                                userEmail,
                                userPhone,
                                rset.getString("password"),
                                null, // TODO: implement connection with company
                                userPermissions,
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate()
                        ));
                    }

                            return Optional.<User>empty();
                        }
                ).orElseThrow();

        assertEquals(user.id(), userFromDB.id());
        assertEquals(user.name().getFirstName(), userFromDB.name().getFirstName());
        assertEquals(user.name().getSecondName(), userFromDB.name().getSecondName());
        assertEquals(user.name().getLastName().orElse(null), userFromDB.name().getLastName().orElse(null));
        assertEquals(user.email().getEmail(), userFromDB.email().getEmail());
        assertEquals(user.phone().getPhone(), userFromDB.phone().getPhone());
        assertEquals(user.password(), userFromDB.password());
        assertEquals(user.companyId(), userFromDB.companyId());
        assertEquals(user.permissions().get(0), userFromDB.permissions().get(0));
        assertEquals(user.createdAt(), userFromDB.createdAt());
        assertEquals(user.updatedAt(), userFromDB.updatedAt());
    }

    @Test
    void getTest() throws SQLException {
        RpUser rpUser = new RpUser(dataSource);

        CreateUser createUser = new CreateUser(
                new UserName("testFirstName", "testSecondName", "testLastName"),
                new Email("example1@example1.com"),
                new Phone("+71234567890"),
                "testPassword",
                UUID.randomUUID(),
                List.of(UserPermission.DELETE)
        );

        User expected = rpUser.add(createUser).orElseThrow();
        User user = rpUser.getByEmail(expected.email().getEmail()).orElseThrow();

        assertEquals(expected.id(), user.id());
        assertEquals(expected.name().getFirstName(), user.name().getFirstName());
        assertEquals(expected.name().getSecondName(), user.name().getSecondName());
        assertEquals(expected.name().getLastName().orElse(null), user.name().getLastName().orElse(null));
        assertEquals(expected.email().getEmail(), user.email().getEmail());
        assertEquals(expected.phone().getPhone(), user.phone().getPhone());
        assertEquals(expected.password(), user.password());
        assertEquals(expected.companyId(), user.companyId());
        assertEquals(expected.permissions().get(0), user.permissions().get(0));
        assertEquals(expected.createdAt(), user.createdAt());
        assertEquals(expected.updatedAt(), user.updatedAt());

    }

}
