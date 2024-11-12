package ru.supply.data.user;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import ru.supply.data.company.Company;
import ru.supply.data.utils.Email;
import ru.supply.data.utils.user.UserName;
import ru.supply.data.utils.Phone;
import ru.supply.data.utils.user.UserPermission;
import ru.supply.requestEntity.user.UserRequestEntity;

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
    // @TODO create connection pool6
    public Optional<User> add(UserRequestEntity user) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Connection connection = dataSource.getConnection();
        Array privilegesArray = connection.createArrayOf("user_privilege", user.permissions().stream().map(UserPermission::name).toArray());

        UUID userId = jdbcSession
                .sql("""
                        INSERT INTO company_user (name, email, phone, password, privileges, created_at, updated_at)
                        VALUES ((?, ?, ?)::USER_NAME, ?::EMAIL, ?::PHONE, ?, ?::user_privilege[], ?, ?)
                        """)
                .set(user.name().getFirstName())
                .set(user.name().getSecondName())
                .set(user.name().getLastName().orElse(null))
                .set(user.email().getEmail())
                .set(user.phone().getPhone())
                .set(user.password())
                .set(privilegesArray)
                .set(LocalDate.now())
                .set(LocalDate.now())
                .insert(new SingleOutcome<>(UUID.class));
        return Optional.of(
                new User(
                        userId,
                        user.name(),
                        user.email(),
                        user.phone(),
                        user.password(),
                        user.companyId(),
                        user.permissions(),
                        LocalDate.now(),
                        LocalDate.now(),
                        List.of()
                )
        );
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
                           c.companyId,
                           array_agg(w.warehouse) AS warehouses
                    FROM company_user u
                    LEFT JOIN company_user_connection c ON u.id = c.userId
                    LEFT JOIN company_user_warehouse w ON u.id = w.userId
                    WHERE u.email = ?
                    GROUP BY u.id, c.companyId
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

    private static Optional<User> compactUserFromResultSet(ResultSet rset) throws SQLException {
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

        UUID companyId = rset.getObject("companyId", UUID.class);

        List<UUID> warehouses = Arrays.stream(
                (UUID[]) rset.getArray("warehouses").getArray()
        ).filter(Objects::nonNull).toList();

        return Optional.of(new User(
                UUID.fromString(rset.getString("id")),
                userName,
                userEmail,
                userPhone,
                rset.getString("password"),
                companyId,
                userPermissions,
                rset.getDate("created_at").toLocalDate(),
                rset.getDate("updated_at").toLocalDate(),
                warehouses
        ));
    }

}
