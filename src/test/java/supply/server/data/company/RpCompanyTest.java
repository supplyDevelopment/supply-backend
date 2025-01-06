package supply.server.data.company;

import com.jcabi.jdbc.JdbcSession;
import supply.server.configuration.DBConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;
import supply.server.requestEntity.company.CompanyRequestEntity;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Testcontainers
public class RpCompanyTest extends DBConnection {

    @Autowired
    private DataSource dataSource;

    @Test
    @Disabled
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
        Assertions.assertEquals(company.contactEmails().get(0).getEmail(), result.contactEmails().get(0).getEmail());
        Assertions.assertEquals(company.contactPhones().get(0).getPhone(), result.contactPhones().get(0).getPhone());
        Assertions.assertEquals(company.bil_address().getBil(), result.bil_address().getBil());
        Assertions.assertEquals(company.tax_id().getTax(), result.tax_id().getTax());
        Assertions.assertEquals(company.addresses().get(0).getAddress(), result.addresses().get(0).getAddress());
        Assertions.assertEquals(company.status(), result.status());
    }

    @Test
    @Disabled
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
        Assertions.assertEquals(company.contactEmails().get(0).getEmail(), result.contactEmails().get(0).getEmail());
        Assertions.assertEquals(company.contactPhones().get(0).getPhone(), result.contactPhones().get(0).getPhone());
        Assertions.assertEquals(company.bil_address().getBil(), result.bil_address().getBil());
        Assertions.assertEquals(company.tax_id().getTax(), result.tax_id().getTax());
        Assertions.assertEquals(company.addresses().get(0).getAddress(), result.addresses().get(0).getAddress());
        Assertions.assertEquals(company.status(), result.status());

    }

}
