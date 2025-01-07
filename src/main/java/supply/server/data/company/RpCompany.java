package supply.server.data.company;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;
import supply.server.requestEntity.company.CompanyRequestEntity;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                         bil_address, tax, addresses, status, created_at)
                         VALUES (?, ?, ?, ?, ?, ?, ?::COMPANY_STATUS, ?)
                        """)
                .set(createCompany.name())
                .set(emailsArray)
                .set(phonesArray)
                .set(createCompany.bil_address().getBil())
                .set(createCompany.tax().getTax())
                .set(addressesArray)
                .set(createCompany.status().toString())
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
                dataSource
        ));
    }

    public Optional<Company> get(UUID companyId) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT id, name, contact_emails, contact_phones,
                         bil_address, tax, addresses, status,
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

        return Optional.of(new Company(
                UUID.fromString(rset.getString("id")),
                rset.getString("name"),
                companyEmails,
                companyPhones,
                new Bil(rset.getString("bil_address")),
                new Tax(rset.getString("tax")),
                companyAddresses,
                CompanyStatus.valueOf(rset.getString("status")),
                rset.getDate("created_at").toLocalDate(),
                rset.getDate("updated_at").toLocalDate(),
                dataSource
        ));
    }
}
