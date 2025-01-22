package supply.server.data.user;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import supply.server.configuration.DataCreator;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.company.Company;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RpUserTest extends DataCreator {

    private final DataSource dataSource = dataSource();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void addTest() throws SQLException {
        RpUser rpUser = new RpUser(dataSource, new BCryptPasswordEncoder());
        CreateUser createUser = generateUser();

        User user = rpUser.add(createUser, getCompany(false).id()).orElseThrow();

        assertEquals(createUser.name().getFirstName(), user.name().getFirstName());
        assertEquals(createUser.name().getSecondName(), user.name().getSecondName());
        assertEquals(createUser.name().getLastName(), user.name().getLastName());
        assertEquals(createUser.email().getEmail(), user.email().getEmail());
        assertEquals(createUser.phone().getPhone(), user.phone().getPhone());
        assertTrue(passwordEncoder.matches(createUser.password(), user.password()));
        assertEquals(createUser.permissions().get(0), user.permissions().get(0));


        JdbcSession jdbcSession = new JdbcSession(dataSource);
        User userFromDB = jdbcSession
                .sql("""
                    SELECT u.id,
                           (u.name).first_name as firstName,
                           (u.name).second_name as secondName,
                           (u.name).last_name as lastName,
                           u.password,
                           u.privileges,
                           u.email,
                           u.phone,
                           u.created_at,
                           u.updated_at,
                           cu.company_id
                    FROM company_user u
                    LEFT JOIN company_users cu ON u.id = cu.user_id
                    WHERE u.id = ?
                    """)
                .set(user.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        Email userEmail = new Email(rset.getString("email"));

                        UserName userName = new UserName(
                                rset.getString("firstName"),
                                rset.getString("secondName"),
                                rset.getString("lastName")                        );
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
                                rset.getObject("company_id", UUID.class),
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
        assertEquals(user.name().getLastName(), userFromDB.name().getLastName());
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
        RpUser rpUser = new RpUser(dataSource, new BCryptPasswordEncoder());

        User expected = getUser(false);
        User user = rpUser.get(expected.email().getEmail()).orElseThrow();

        assertEquals(expected.id(), user.id());
        assertEquals(expected.name().getFirstName(), user.name().getFirstName());
        assertEquals(expected.name().getSecondName(), user.name().getSecondName());
        assertEquals(expected.name().getLastName(), user.name().getLastName());
        assertEquals(expected.email().getEmail(), user.email().getEmail());
        assertEquals(expected.phone().getPhone(), user.phone().getPhone());
        assertEquals(expected.password(), user.password());
        assertEquals(expected.companyId(), user.companyId());
        assertEquals(expected.permissions().get(0), user.permissions().get(0));
        assertEquals(expected.createdAt(), user.createdAt());
        assertEquals(expected.updatedAt(), user.updatedAt());
    }

    @Test
    void getByIdTest() throws SQLException {
        RpUser rpUser = new RpUser(dataSource, new BCryptPasswordEncoder());

        CreateUser createUser = generateUser();

        User expected = rpUser.add(createUser, getCompany(false).id()).orElseThrow();
        User user = rpUser.get(expected.id(), getCompany(false).id()).orElseThrow();

        assertEquals(expected.id(), user.id());
        assertEquals(expected.name().getFirstName(), user.name().getFirstName());
        assertEquals(expected.name().getSecondName(), user.name().getSecondName());
        assertEquals(expected.name().getLastName(), user.name().getLastName());
        assertEquals(expected.email().getEmail(), user.email().getEmail());
        assertEquals(expected.phone().getPhone(), user.phone().getPhone());
        assertEquals(expected.password(), user.password());
        assertEquals(expected.companyId(), user.companyId());
        assertEquals(expected.permissions().get(0), user.permissions().get(0));
        assertEquals(expected.createdAt(), user.createdAt());
        assertEquals(expected.updatedAt(), user.updatedAt());

        assertTrue(rpUser.get(expected.id(), UUID.randomUUID()).isEmpty());
    }

    @Test
    void getAll() throws SQLException {
        List<UUID> companyIds = getCompanies(2, true).stream().map(Company::id).toList();

        RpUser rpUser = new RpUser(dataSource, new BCryptPasswordEncoder());
        String[] additionLetters = new String[]{
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p"
        };
        List<User> expected = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (i % 4 == 0) {
                expected.add(rpUser.add(new CreateUser(
                        new UserName("addingFirstName" + additionLetters[i], "addingSecondName" + additionLetters[i], "addingLastName" + additionLetters[i]),
                        new Email("example" + i + "@example.com"),
                        new Phone("+7123456789" + i),
                        "testPassword",
                        List.of(UserPermission.DELETE)
                ), companyIds.get(0)).orElseThrow());
            } else if (i % 2 == 0) {
                rpUser.add(new CreateUser(
                        new UserName("addingFirstName" + additionLetters[i], "addingSecondName" + additionLetters[i], "addingLastName" + additionLetters[i]),
                        new Email("example" + i + "@example.com"),
                        new Phone("+7123456789" + i),
                        "testPassword",
                        List.of(UserPermission.DELETE)
                ), companyIds.get(1)).orElseThrow();
            } else {
                rpUser.add(new CreateUser(
                        new UserName("agettingFirstName" + additionLetters[i], "agettingSecondName" + additionLetters[i], "agettingLastName" + additionLetters[i]),
                        new Email("example" + i + "@example.com"),
                        new Phone("+7123456789" + i),
                        "testPassword",
                        List.of(UserPermission.DELETE)
                ), companyIds.get(0));
            }
        }

        PaginatedList<User> users = rpUser.getAll("adding", companyIds.get(0), new Pagination(20, 0));
        assertEquals(2, users.total());
        for (User user: users.items()) {
            for (User expectedUser: expected) {
                if (user.id().equals(expectedUser.id())) {
                    assertEquals(expectedUser.id(), user.id());
                    assertEquals(expectedUser.name().getFirstName(), user.name().getFirstName());
                    assertEquals(expectedUser.name().getSecondName(), user.name().getSecondName());
                    assertEquals(expectedUser.name().getLastName(), user.name().getLastName());
                    assertEquals(expectedUser.email().getEmail(), user.email().getEmail());
                    assertEquals(expectedUser.phone().getPhone(), user.phone().getPhone());
                    assertEquals(expectedUser.password(), user.password());
                    assertEquals(expectedUser.companyId(), user.companyId());
                    assertEquals(expectedUser.permissions().get(0), user.permissions().get(0));
                    assertEquals(expectedUser.createdAt(), user.createdAt());
                    assertEquals(expectedUser.updatedAt(), user.updatedAt());
                }
            }
        }
        assertEquals(4, rpUser.getAll("agetting", companyIds.get(0), new Pagination(20, 0)).total());
        assertEquals(6, rpUser.getAll("a", companyIds.get(0), new Pagination(20, 0)).total());
        assertEquals(2, rpUser.getAll("", companyIds.get(1), new Pagination(20, 0)).total());

        expected.add(rpUser.add(new CreateUser(
                new UserName("gettingFirstNameas", "gettingSecondNameas", "gettingLastNameas"),
                new Email("aexample" + 1 + "@example.com"),
                new Phone("+70234567899"),
                "testPassword",
                List.of(UserPermission.DELETE)
        ), companyIds.get(0)).orElseThrow());

        PaginatedList<User> users2 = rpUser.getAll("a", companyIds.get(0), new Pagination(20, 0));

        assertEquals(
                expected.get(expected.size() - 1).id(),
                users2.items().get(users2.items().size() - 1).id()
        );

    }

}
