package supply.server.data.company;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@AllArgsConstructor
public class RpCompany {

    public final DataSource dataSource;

    public Optional<Company> add(CreateCompany createCompany) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Connection connection = dataSource.getConnection();

        Array emailsArray = connection.createArrayOf("EMAIL",
                createCompany.contactEmails().stream().map(Email::getEmail).toArray());
        Array phonesArray = connection.createArrayOf("PHONE",
                createCompany.contactPhones().stream().map(Phone::getPhone).toArray());
        Array addressesArray = connection.createArrayOf("VARCHAR",
                createCompany.addresses().stream().map(Address::getAddress).toArray());
        UUID companyId = jdbcSession
                .sql("""
                        INSERT INTO company (name, contact_emails, contact_phones,
                         bil_address, tax, addresses, status, expires_at, created_at)
                         VALUES (?, ?, ?, ?, ?, ?, ?::COMPANY_STATUS, ?, ?)
                        """)
                .set(createCompany.name())
                .set(emailsArray)
                .set(phonesArray)
                .set(Objects.isNull(createCompany.bil_address()) ? null : createCompany.bil_address().getBil())
                .set(Objects.isNull(createCompany.tax()) ? null : createCompany.tax().getTax())
                .set(addressesArray)
                .set(createCompany.status().toString())
                .set(LocalDate.now())
                .set(LocalDate.now())
                .insert(new SingleOutcome<>(UUID.class));

        return Optional.of(new Company(
                companyId,
                createCompany.name(),
                createCompany.contactEmails(),
                createCompany.contactPhones(),
                createCompany.bil_address(),
                createCompany.tax(),
                createCompany.addresses(),
                createCompany.status(),
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now()
        ));
    }

    public Optional<Company> extendSubscription(int months, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Optional<LocalDate> expiresAt = jdbcSession
                .sql("""
                        SELECT expires_at
                        FROM company
                        WHERE id = ?
                        """)
                .set(companyId)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return Optional.of(rset.getDate("expires_at").toLocalDate());
                    }
                    return Optional.empty();
                });
        if (expiresAt.isEmpty()) {
            return Optional.empty();
        }
        LocalDate extendedExpiresAt = expiresAt.get().plusMonths(months);
        jdbcSession
                .sql("""
                        UPDATE company
                        SET expires_at = ?
                        WHERE id = ?
                        """)
                .set(extendedExpiresAt)
                .set(companyId)
                .update(Outcome.VOID);
        return get(companyId);
    }

    public Optional<Company> get(UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT id, name, contact_emails, contact_phones,
                         bil_address, tax, addresses, status, expires_at,
                         created_at, updated_at
                        FROM company
                        WHERE id = ?
                        """)
                .set(companyId)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return compactCompanyFromResultSet(rset);
                    }
                    return Optional.empty();
                });
    }

    public boolean projectCheck(UUID projectId, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT project
                        FROM company_projects
                        WHERE company = ? AND project = ?
                        """)
                .set(companyId)
                .set(projectId)
                .select((rset, stmt) -> rset.next());
    }

    public boolean warehouseCheck(UUID warehouseId, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT warehouse
                        FROM company_warehouses
                        WHERE company = ? AND warehouse = ?
                        """)
                .set(companyId)
                .set(warehouseId)
                .select((rset, stmt) -> rset.next());
    }

    public boolean userCheck(UUID userId, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT user_id
                        FROM company_users
                        WHERE company_id = ? AND user_id = ?
                        """)
                .set(companyId)
                .set(userId)
                .select((rset, stmt) -> rset.next());
    }

    public boolean resourceCheck(UUID resourceId, UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                SELECT 1
                FROM resource r
                INNER JOIN company_warehouses cw
                ON r.warehouseId = cw.warehouse
                WHERE r.id = ? AND cw.company = ?
                """)
                .set(resourceId)
                .set(companyId)
                .select((rset, stmt) -> rset.next());
    }

    private Optional<Company> compactCompanyFromResultSet(ResultSet rset) throws SQLException {
        String emails = rset.getString("contact_emails");
        emails = emails.substring(1, emails.length() - 1);
        List<Email> companyEmails = Arrays.stream(
                emails.split(",")
        ).filter(string -> !string.isEmpty()).map(Email::new).toList();

        String phones = rset.getString("contact_phones");
        phones = phones.substring(1, phones.length() - 1);
        List<Phone> companyPhones = Arrays.stream(
                phones.split(",")
        ).filter(string -> !string.isEmpty()).map(Phone::new).toList();

        String addresses = rset.getString("addresses");
        addresses = addresses.substring(1, addresses.length() - 1);
        List<Address> companyAddresses = Arrays.stream(
                addresses.split(",")
        ).filter(string -> !string.isEmpty()).map(Address::new).toList();

        String bill = rset.getString("bil_address");
        String tax = rset.getString("tax");

        return Optional.of(new Company(
                UUID.fromString(rset.getString("id")),
                rset.getString("name"),
                companyEmails,
                companyPhones,
                new Bil(Objects.isNull(bill) ? null : bill),
                new Tax(Objects.isNull(tax) ? null : tax),
                companyAddresses,
                CompanyStatus.valueOf(rset.getString("status")),
                rset.getDate("expires_at").toLocalDate(),
                rset.getDate("created_at").toLocalDate(),
                rset.getDate("updated_at").toLocalDate()
        ));
    }
}
