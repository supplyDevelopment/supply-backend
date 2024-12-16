package ru.supply.data.company;

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.SingleOutcome;
import lombok.AllArgsConstructor;
import ru.supply.data.utils.Address;
import ru.supply.data.utils.Email;
import ru.supply.data.utils.Phone;
import ru.supply.data.utils.company.Bil;
import ru.supply.data.utils.company.CompanyStatus;
import ru.supply.data.utils.company.Tax;
import ru.supply.requestEntity.company.CompanyRequestEntity;

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

    public Company add(CompanyRequestEntity company) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Connection connection = dataSource.getConnection();

        Array emailsArray = connection.createArrayOf("EMAIL",
                company.contactEmails().stream().map(Email::getEmail).toArray());
        Array phonesArray = connection.createArrayOf("PHONE",
                company.contactPhones().stream().map(Phone::getPhone).toArray());
        Array addressesArray = connection.createArrayOf("VARCHAR",
                company.addresses().stream().map(Address::getAddress).toArray());
        UUID companyId = jdbcSession
                .sql("""
                        INSERT INTO company (name, contact_emails, contact_phones, bil_address, tax_id, addresses, status, updated_at)
                         VALUES (?, ?, ?, ?, ?, ?, ?::COMPANY_STATUS, ?)
                        """)
                .set(company.name())
                .set(emailsArray)
                .set(phonesArray)
                .set(company.bil_address().getBil())
                .set(company.tax_id().getTax())
                .set(addressesArray)
                .set(company.status().toString())
                .set(LocalDate.now())
                .insert(new SingleOutcome<>(UUID.class));

        return new Company(
                companyId,
                company.name(),
                company.contactEmails(),
                company.contactPhones(),
                company.bil_address(),
                company.tax_id(),
                company.addresses(),
                company.status()
        );
    }

    public Optional<Company> getByName(String name) throws SQLException {
        JdbcSession jdbcSession = new JdbcSession(dataSource);
        return jdbcSession
                .sql("""
                        SELECT * FROM company
                        WHERE name = ?
                        """)
                .set(name)
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return compactCompanyFromResultSet(rset);
                    }
                    return Optional.empty();
                });
    }

    static Optional<Company> compactCompanyFromResultSet(ResultSet rset) throws SQLException {
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
                new Tax(rset.getString("tax_id")),
                companyAddresses,
                CompanyStatus.valueOf(rset.getString("status"))
        ));
    }

}
