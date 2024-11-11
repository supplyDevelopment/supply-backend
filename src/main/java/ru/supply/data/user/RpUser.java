package ru.supply.data.user;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import ru.supply.data.utils.Email;
import ru.supply.data.utils.UserName;
import ru.supply.data.utils.Phone;
import ru.supply.data.utils.UserPermission;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class RpUser {

    public final DataSource dataSource;

    public Optional<User> getByEmail(String email) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT * FROM users
                        WHERE email = ?
                        """)
                .set(email)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        Email userEmail = new Email(rset.getString("email"));
                        UserName userName = new UserName(
                                rset.getString("name.firstName"),
                                rset.getString("name.secondName"),
                                rset.getString("name.lastName")
                        );
                        Phone userPhone = new Phone(rset.getString("phone"));
                        List<UserPermission> userPermissions = Arrays.stream(
                                (String[]) rset.getArray("privileges").getArray()
                        ).map(UserPermission::valueOf).toList();
                        List<UUID> warehouses = Arrays.stream(
                                (String[]) rset.getArray("warehouses").getArray()
                        ).map(UUID::fromString).toList();

                        return Optional.of(new User(
                                UUID.fromString(rset.getString("id")),
                                userName,
                                userEmail,
                                userPhone,
                                rset.getString("password"),
                                UUID.fromString(rset.getString("companyId")),
                                userPermissions,
                                rset.getDate("createdAt").toLocalDate(),
                                rset.getDate("updatedAt").toLocalDate(),
                                warehouses
                        ));
                    } else {
                        return Optional.empty();
                    }
                });
    }
    public Optional<User> add(User user) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        UUID userId = jdbcSession
                .sql("""
                        INSERT INTO users (name, email, phone, password, company_id, privileges, warehouses, created_at, updated_at)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """)
                .set(user.name().getFirstName())
                .set(user.name().getSecondName())
                .set(user.name().getLastName())
                .set(user.email().toString())
                .set(user.phone().toString())
                .set(user.password())
                .set(user.companyId())
                .set(user.permissions())
                .set(user.warehouses())
                .set(LocalDate.now())
                .set(LocalDate.now())
                .insert(new SingleOutcome<>(UUID.class));
        return Optional.empty();
    }
}
