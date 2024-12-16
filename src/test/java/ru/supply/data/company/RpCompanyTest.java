package ru.supply.data.company;

import com.jcabi.jdbc.JdbcSession;
import configuration.DBConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.supply.data.utils.Address;
import ru.supply.data.utils.Email;
import ru.supply.data.utils.Phone;
import ru.supply.data.utils.company.Bil;
import ru.supply.data.utils.company.CompanyStatus;
import ru.supply.data.utils.company.Tax;
import ru.supply.requestEntity.company.CompanyRequestEntity;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Testcontainers
public class RpCompanyTest extends DBConnection {

    @Autowired
    private DataSource dataSource;

    @Test
    void addTest() throws Exception {
        RpCompany rpCompany = new RpCompany(dataSource);
        CompanyRequestEntity company = new CompanyRequestEntity(
                "addTestCompany",
                List.of(new Email("example@example.com")),
                List.of(new Phone("+71234567890")),
                new Bil("1234567890"),
                new Tax("1234567890"),
                List.of(new Address("test")),
                CompanyStatus.of("active")
        );
        rpCompany.add(company);

        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Optional<Company> resultOpt = jdbcSession.sql("""
                SELECT * FROM company
                WHERE name = ?
                """)
                .set("addTestCompany")
                .select((rset, stmt) -> {
                    if (rset.next()) {
                        return RpCompany.compactCompanyFromResultSet(rset);
                    }
                    return Optional.empty();
                });
        Assertions.assertTrue(resultOpt.isPresent());
        Company result = resultOpt.get();

        Assertions.assertEquals(company.name(), result.name());
        Assertions.assertEquals(company.contactEmails().getFirst().getEmail(), result.contactEmails().getFirst().getEmail());
        Assertions.assertEquals(company.contactPhones().getFirst().getPhone(), result.contactPhones().getFirst().getPhone());
        Assertions.assertEquals(company.bil_address().getBil(), result.bil_address().getBil());
        Assertions.assertEquals(company.tax_id().getTax(), result.tax_id().getTax());
        Assertions.assertEquals(company.addresses().getFirst().getAddress(), result.addresses().getFirst().getAddress());
        Assertions.assertEquals(company.status(), result.status());
    }

    @Test
    void getByNameTest() throws Exception {
        RpCompany rpCompany = new RpCompany(dataSource);
        CompanyRequestEntity company = new CompanyRequestEntity(
                "getByNameTestCompany",
                List.of(new Email("example@example.com")),
                List.of(new Phone("+71234567890")),
                new Bil("234567890"),
                new Tax("234567890"),
                List.of(new Address("test")),
                CompanyStatus.of("active")
        );
        rpCompany.add(company);

        Optional<Company> resultOpt = rpCompany.getByName("getByNameTestCompany");
        Assertions.assertTrue(resultOpt.isPresent());
        Company result = resultOpt.get();

        Assertions.assertEquals(company.name(), result.name());
        Assertions.assertEquals(company.contactEmails().getFirst().getEmail(), result.contactEmails().getFirst().getEmail());
        Assertions.assertEquals(company.contactPhones().getFirst().getPhone(), result.contactPhones().getFirst().getPhone());
        Assertions.assertEquals(company.bil_address().getBil(), result.bil_address().getBil());
        Assertions.assertEquals(company.tax_id().getTax(), result.tax_id().getTax());
        Assertions.assertEquals(company.addresses().getFirst().getAddress(), result.addresses().getFirst().getAddress());
        Assertions.assertEquals(company.status(), result.status());

    }

}
