package supply.server.data.supplier;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class RpSupplier {

    private final DataSource dataSource;

    public Optional<Supplier> add(Supplier supplier) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Connection connection = dataSource.getConnection();

        Array emailsArray = connection.createArrayOf("EMAIL",
                supplier.emails().stream().map(Email::getEmail).toArray());
        Array phonesArray = connection.createArrayOf("PHONE",
                supplier.phones().stream().map(Phone::getPhone).toArray());

        UUID supplierId = jdbcSession
                .sql("""
                    INSERT INTO supplier (name, contact_emails, contact_phones, address)
                    VALUES (?, ?, ?, ?)
                    RETURNING id
                    """)
                .set(supplier.name())
                .set(emailsArray)
                .set(phonesArray)
                .set(supplier.address())
                .select(new SingleOutcome<>(UUID.class));
        return Optional.of(new Supplier(
                supplierId,
                supplier.name(),
                supplier.emails(),
                supplier.phones(),
                supplier.address(),
                supplier.createdAt(),
                supplier.updatedAt())
        );
    }

    public Optional<Supplier> getById(UUID id) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                    SELECT id, name, contact_emails, contact_phones, address, created_at, updated_at
                    FROM supplier
                    WHERE id = ?
                    """)
                .set(id)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return compactSupplierFromResultSet(rset);
                    } else {
                        return Optional.empty();
                    }
                });
    }

    private Optional<Supplier> compactSupplierFromResultSet(ResultSet rset) throws SQLException {
        String emails = rset.getString("contact_emails");
        emails = emails.substring(1, emails.length() - 1);
        List<Email> supplierEmails = Arrays.stream(
                emails.split(",")
        ).filter(string -> !string.isEmpty()).map(Email::new).toList();

        String phones = rset.getString("contact_phones");
        phones = phones.substring(1, phones.length() - 1);
        List<Phone> supplierPhones = Arrays.stream(
                phones.split(",")
        ).filter(string -> !string.isEmpty()).map(Phone::new).toList();

        Date updatedAtDate = rset.getDate("updated_at");
        LocalDate updatedAt = null;
        if (updatedAtDate != null) {
            updatedAt = updatedAtDate.toLocalDate();
        }

        return Optional.of(new Supplier(
                rset.getObject("id", UUID.class),
                rset.getString("name"),
                supplierEmails,
                supplierPhones,
                rset.getString("address"),
                rset.getDate("created_at").toLocalDate(),
                updatedAt
        ));
    }
}
