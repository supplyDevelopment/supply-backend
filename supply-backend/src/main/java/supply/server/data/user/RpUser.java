package supply.server.data.user;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@AllArgsConstructor
public class RpUser {

    public final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> add(CreateUser createUser, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Connection connection = dataSource.getConnection();
        Array privilegesArray = connection.createArrayOf("user_privilege", createUser.permissions().stream().map(UserPermission::name).toArray());

        String encodedPassword = passwordEncoder.encode(createUser.password());

        UUID userId = jdbcSession
                .sql("""
                        INSERT INTO company_user (name, email, phone, password, privileges, created_at)
                        VALUES ((?, ?, ?)::USER_NAME, ?::EMAIL, ?::PHONE, ?, ?::user_privilege[], ?)
                        """)
                .set(Objects.isNull(createUser.name()) ? null : createUser.name().getFirstName())
                .set(Objects.isNull(createUser.name()) ? null : createUser.name().getSecondName())
                .set(Objects.isNull(createUser.name()) ? null : createUser.name().getLastName())
                .set(createUser.email().getEmail())
                .set(Objects.isNull(createUser.phone()) ? null : createUser.phone().getPhone())
                .set(encodedPassword)
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
                .set(companyId)
                .insert(Outcome.VOID);

        connection.close();
        return Optional.of(
                new User(
                        userId,
                        createUser.name(),
                        createUser.email(),
                        createUser.phone(),
                        encodedPassword,
                        companyId,
                        createUser.permissions(),
                        LocalDate.now(),
                        LocalDate.now()
                )
        );
    }

    public Optional<User> get(UUID userId, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                    SELECT u.id,
                           (u.name).first_name AS first_name,
                           (u.name).second_name AS second_name,
                           (u.name).last_name AS last_name,
                           u.password,
                           u.privileges,
                           u.email,
                           u.phone,
                           u.created_at,
                           u.updated_at,
                           cu.company_id
                    FROM company_user u
                    LEFT JOIN company_users cu ON u.id = cu.user_id
                    WHERE u.id = ? AND cu.company_id = ?
                    """)
                .set(userId)
                .set(companyId)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return compactUserFromResultSet(rset);
                    } else {
                        return Optional.empty();
                    }
                });
    }

    public Optional<User> get(String email) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                    SELECT u.id,
                           (u.name).first_name as first_name,
                           (u.name).second_name as second_name,
                           (u.name).last_name as last_name,
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

    public Optional<User> updatePassword(UUID userId, String password, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        String encodedPassword = passwordEncoder.encode(password);
        jdbcSession
                .sql("""
                    UPDATE company_user
                    SET password = ?
                    WHERE id = (
                        SELECT user_id
                        FROM company_users
                        WHERE user_id = ? AND company_id = ?
                    )
                    """)
                .set(encodedPassword)
                .set(userId)
                .set(companyId)
                .update(Outcome.VOID);

        return get(userId, companyId);
    }

    // TODO: implement filters
    public PaginatedList<User> getAll(String prefix, UUID companyId, Pagination pagination) throws SQLException {
        String SQLWith = """
                WITH params AS (SELECT ? AS lower_prefix),
                     user_table AS (
                         SELECT
                             u.id,
                             (u.name).first_name AS first_name,
                             (u.name).second_name AS second_name,
                             (u.name).last_name AS last_name,
                             u.email,
                             u.phone,
                             u.password,
                             u.privileges,
                             u.created_at,
                             u.updated_at,
                             cu.company_id,
                             CASE
                                 WHEN lower(concat((u.name).first_name, ' ', (u.name).second_name, ' ', (u.name).last_name)) LIKE concat(lower_prefix, '%')
                                     OR lower((u.name).first_name) LIKE concat(lower_prefix, '%')
                                     OR lower((u.name).second_name) LIKE concat(lower_prefix, '%')
                                     OR lower((u.name).last_name) LIKE concat(lower_prefix, '%')
                                     THEN 1
                                 WHEN lower(u.email) LIKE concat(lower_prefix, '%')
                                     THEN 2
                                 ELSE 3
                             END AS priority
                         FROM company_user u
                         INNER JOIN company_users cu ON u.id = cu.user_id
                         JOIN params ON true
                         WHERE cu.company_id = ?
                     )
                SELECT *,
                       (SELECT COUNT(*) FROM user_table WHERE priority <= 2) AS total_count
                FROM user_table
                WHERE priority <= 2
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
                    List<User> users = new ArrayList<>();
                    long total = 0;

                    while (rset.next()) {
                        if (total == 0) {
                            total = rset.getLong("total_count");
                        }
                        Optional<User> user = compactUserFromResultSet(rset);
                        user.ifPresent(users::add);
                    }

                    return new PaginatedList<>(total, users);
                });
    }


    private Optional<User> compactUserFromResultSet(ResultSet rset) throws SQLException {
        Email userEmail = new Email(rset.getString("email"));
        UserName userName;
        if (Objects.isNull(rset.getString("first_name")) || Objects.isNull(rset.getString("second_name"))) {
            userName = null;
        } else {
            userName = new UserName(
                    rset.getString("first_name"),
                    rset.getString("second_name"),
                    rset.getString("last_name")
            );
        }
        Phone userPhone = Objects.isNull(rset.getString("phone")) ? null : new Phone(rset.getString("phone"));
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
