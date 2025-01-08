package supply.server.data.user;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import supply.server.data.utils.Email;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.Phone;
import supply.server.data.utils.user.UserPermission;
import supply.server.requestEntity.user.UserRequestEntity;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
public class RpUser {

    public final DataSource dataSource;

    public Optional<User> add(CreateUser createUser) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Connection connection = dataSource.getConnection();
        Array privilegesArray = connection.createArrayOf("user_privilege", createUser.permissions().stream().map(UserPermission::name).toArray());

        UUID userId = jdbcSession
                .sql("""
                        INSERT INTO company_user (name, email, phone, password, privileges, created_at)
                        VALUES ((?, ?, ?)::USER_NAME, ?::EMAIL, ?::PHONE, ?, ?::user_privilege[], ?)
                        """)
                .set(createUser.name().getFirstName())
                .set(createUser.name().getSecondName())
                .set(createUser.name().getLastName().orElse(null))
                .set(createUser.email().getEmail())
                .set(createUser.phone().getPhone())
                .set(createUser.password())
                .set(privilegesArray)
                .set(LocalDate.now())
                .insert(new SingleOutcome<>(UUID.class));

        jdbcSession
                .sql("""
                        INSERT INTO company_users
                        (user_id, company_id)
                        VALUES (?, ?)
                        """)
                .set(userId)
                .set(createUser.companyId())
                .insert(Outcome.VOID);

        return Optional.of(
                new User(
                        userId,
                        createUser.name(),
                        createUser.email(),
                        createUser.phone(),
                        createUser.password(),
                        createUser.companyId(),
                        createUser.permissions(),
                        LocalDate.now(),
                        LocalDate.now()
                )
        );
    }

    public Optional<User> get(UUID id) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
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
                .set(id)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return compactUserFromResultSet(rset);
                    } else {
                        return Optional.empty();
                    }
                });
    }

    public Optional<User> getByEmail(String email) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
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
                    WHERE u.email = ?
                    """)
                .set(email)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return compactUserFromResultSet(rset);
                    } else {
                        return Optional.empty();
                    }
                });
    }

    private Optional<User> compactUserFromResultSet(ResultSet rset) throws SQLException {
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

}
