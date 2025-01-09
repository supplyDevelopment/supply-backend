package supply.server.data.user;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Test;
import supply.server.configuration.DBConnection;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.company.Company;
import supply.server.data.company.CreateCompany;
import supply.server.data.company.RpCompany;
import supply.server.data.project.Project;
import supply.server.data.project.RpProject;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                getCompanyId(),
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
    void getByEmailTest() throws SQLException {
        RpUser rpUser = new RpUser(dataSource);

        CreateUser createUser = new CreateUser(
                new UserName("testFirstName", "testSecondName", "testLastName"),
                new Email("example1@example1.com"),
                new Phone("+71234567891"),
                "testPassword",
                getCompanyId(),
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

    @Test
    void getByIdTest() throws SQLException {
        RpUser rpUser = new RpUser(dataSource);

        CreateUser createUser = new CreateUser(
                new UserName("testFirstName", "testSecondName", "testLastName"),
                new Email("example2@example2.com"),
                new Phone("+71234567892"),
                "testPassword",
                getCompanyId(),
                List.of(UserPermission.DELETE)
        );

        User expected = rpUser.add(createUser).orElseThrow();
        User user = rpUser.get(expected.id()).orElseThrow();

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

    @Test
    void getAllByName() throws SQLException {
        RpCompany rpCompany = new RpCompany(dataSource);
        UUID company1Id = rpCompany.add(new CreateCompany(
                "thirdTestCompany",
                List.of(new Email("examp1le@example.com")),
                List.of(new Phone("+71234567895")),
                new Bil("1234567895"),
                new Tax("1234567895"),
                List.of(new Address("test5")),
                CompanyStatus.ACTIVE
        )).map(Company::id).orElseThrow();
        UUID company2Id = rpCompany.add(new CreateCompany(
                "secondTestCompany",
                List.of(new Email("exampl1e@example.com")),
                List.of(new Phone("+71234567891")),
                new Bil("1234567891"),
                new Tax("1234567891"),
                List.of(new Address("test1")),
                CompanyStatus.ACTIVE
        )).map(Company::id).orElseThrow();

        RpUser rpUser = new RpUser(dataSource);
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
                        company1Id,
                        List.of(UserPermission.DELETE)
                )).orElseThrow());
            } else if (i % 2 == 0) {
                rpUser.add(new CreateUser(
                        new UserName("addingFirstName" + additionLetters[i], "addingSecondName" + additionLetters[i], "addingLastName" + additionLetters[i]),
                        new Email("example" + i + "@example.com"),
                        new Phone("+7123456789" + i),
                        "testPassword",
                        company2Id,
                        List.of(UserPermission.DELETE)
                )).orElseThrow();
            } else {
                rpUser.add(new CreateUser(
                        new UserName("agettingFirstName" + additionLetters[i], "agettingSecondName" + additionLetters[i], "agettingLastName" + additionLetters[i]),
                        new Email("example" + i + "@example.com"),
                        new Phone("+7123456789" + i),
                        "testPassword",
                        company1Id,
                        List.of(UserPermission.DELETE)
                ));
            }
        }

        PaginatedList<User> users = rpUser.getAllByName("adding", company1Id, new Pagination(20, 0));
        assertEquals(2, users.total());
        for (User user: users.items()) {
            for (User expectedUser: expected) {
                if (user.id().equals(expectedUser.id())) {
                    assertEquals(expectedUser.id(), user.id());
                    assertEquals(expectedUser.name().getFirstName(), user.name().getFirstName());
                    assertEquals(expectedUser.name().getSecondName(), user.name().getSecondName());
                    assertEquals(expectedUser.name().getLastName().orElse(null), user.name().getLastName().orElse(null));
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
        assertEquals(4, rpUser.getAllByName("agetting", company1Id, new Pagination(20, 0)).total());
        assertEquals(6, rpUser.getAllByName("a", company1Id, new Pagination(20, 0)).total());
        assertEquals(2, rpUser.getAllByName("", company2Id, new Pagination(20, 0)).total());
    }

}
