package supply.server.data.company;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import supply.server.configuration.DataCreator;
import supply.server.data.project.Project;
import supply.server.data.project.RpProject;
import supply.server.data.user.RpUser;
import supply.server.data.user.User;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;
import supply.server.data.warehouse.RpWarehouse;
import supply.server.data.warehouse.Warehouse;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RpCompanyTest extends DataCreator {

    private final DataSource dataSource = dataSource();

    @Test
    void addTest() throws Exception {
        RpCompany rpCompany = new RpCompany(dataSource);

        CreateCompany createCompany = new CreateCompany(
                null,
                List.of(new Email("example@example.com")),
                List.of(),
                null,
                null,
                List.of(),
                CompanyStatus.ACTIVE
        );
        Company company = rpCompany.add(createCompany).orElseThrow();

        Assertions.assertEquals(createCompany.name(), company.name());
        Assertions.assertEquals(createCompany.contactEmails().stream().map(Email::getEmail).toList(), company.contactEmails().stream().map(Email::getEmail).toList());
        Assertions.assertEquals(createCompany.contactPhones().stream().map(Phone::getPhone).toList(), company.contactPhones().stream().map(Phone::getPhone).toList());
        Assertions.assertEquals(
                Objects.isNull(createCompany.bil_address()) ? null : createCompany.bil_address().getBil(),
                Objects.isNull(company.bil_address()) ? null : company.bil_address().getBil()
        );
        Assertions.assertEquals(
                Objects.isNull(createCompany.tax()) ? null : createCompany.tax().getTax(),
                Objects.isNull(company.tax()) ? null : company.tax().getTax()
        );
        Assertions.assertEquals(createCompany.addresses().stream().map(Address::getAddress).toList(), company.addresses().stream().map(Address::getAddress).toList());
        Assertions.assertEquals(createCompany.status(), company.status());

        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Company actual = jdbcSession
                .sql("""
                        SELECT id, name, contact_emails, contact_phones,
                         bil_address, tax, addresses, status, expires_at,
                         created_at, updated_at
                        FROM company
                        WHERE id = ?
                        """)
                .set(company.id())
                .select((rset, stmt) -> {
                    if (rset.next()) {
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
                                rset.getDate("expires_at").toLocalDate(),
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate()
                        ));
                    }
                    return Optional.<Company>empty();
                }).orElseThrow();

        Assertions.assertEquals(actual.id(), company.id());
        Assertions.assertEquals(actual.name(), company.name());
        Assertions.assertEquals(actual.contactEmails().stream().map(Email::getEmail).toList(), company.contactEmails().stream().map(Email::getEmail).toList());
        Assertions.assertEquals(actual.contactPhones().stream().map(Phone::getPhone).toList(), company.contactPhones().stream().map(Phone::getPhone).toList());
        Assertions.assertEquals(
                Objects.isNull(actual.bil_address()) ? null : actual.bil_address().getBil(),
                Objects.isNull(company.bil_address()) ? null : company.bil_address().getBil()
        );
        Assertions.assertEquals(
                Objects.isNull(actual.tax()) ? null : actual.tax().getTax(),
                Objects.isNull(company.tax()) ? null : company.tax().getTax()
        );
        Assertions.assertEquals(actual.addresses().stream().map(Address::getAddress).toList(), company.addresses().stream().map(Address::getAddress).toList());
        Assertions.assertEquals(actual.status(), company.status());
        Assertions.assertEquals(actual.createdAt(), company.createdAt());
        Assertions.assertEquals(actual.updatedAt(), company.updatedAt());
    }

    @Test
    void getTest() throws Exception {
        RpCompany rpCompany = new RpCompany(dataSource);
        Company expected = rpCompany.add(new CreateCompany(
                null,
                List.of(new Email("example@example.com")),
                List.of(),
                null,
                null,
                List.of(),
                CompanyStatus.ACTIVE
        )).orElseThrow();
        Company actual = rpCompany.get(expected.id()).orElseThrow();

        Assertions.assertEquals(expected.id(), actual.id());
        Assertions.assertEquals(expected.name(), actual.name());
        Assertions.assertEquals(expected.contactEmails().stream().map(Email::getEmail).toList(), actual.contactEmails().stream().map(Email::getEmail).toList());
        Assertions.assertEquals(expected.contactPhones().stream().map(Phone::getPhone).toList(), actual.contactPhones().stream().map(Phone::getPhone).toList());
        Assertions.assertEquals(
                Objects.isNull(expected.bil_address()) ? null : expected.bil_address().getBil(),
                Objects.isNull(actual.bil_address()) ? null : actual.bil_address().getBil()
        );
        Assertions.assertEquals(
                Objects.isNull(expected.tax()) ? null : expected.tax().getTax(),
                Objects.isNull(actual.tax()) ? null : actual.tax().getTax()
        );
        Assertions.assertEquals(expected.addresses().stream().map(Address::getAddress).toList(), actual.addresses().stream().map(Address::getAddress).toList());
        Assertions.assertEquals(expected.status(), actual.status());
        Assertions.assertEquals(expected.createdAt(), actual.createdAt());
        Assertions.assertEquals(expected.updatedAt(), actual.updatedAt());
    }

    @Test
    void checkProjectTest() throws SQLException {
        RpCompany rpCompany = new RpCompany(dataSource);
        List<UUID> companyIds = getCompanies(2, false).stream().map(Company::id).toList();

        RpProject rpProject = new RpProject(dataSource);
        Project project1 = rpProject.add("testName", "testDescription", companyIds.get(0)).orElseThrow();
        Project project2 = rpProject.add("testName", "testDescription", companyIds.get(1)).orElseThrow();

        assertTrue(rpCompany.projectCheck(project1.id(), companyIds.get(0)));
        assertFalse(rpCompany.projectCheck(project2.id(), companyIds.get(0)));
    }

    @Test
    void checkWarehouseTest() throws SQLException {
        RpCompany rpCompany = new RpCompany(dataSource);
        List<UUID> companyIds = getCompanies(2, false).stream().map(Company::id).toList();

        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);
        Warehouse warehouse1 = rpWarehouse.add(generateWarehouse(List.of()), companyIds.get(0)).orElseThrow();
        Warehouse warehouse2 = rpWarehouse.add(generateWarehouse(List.of()), companyIds.get(1)).orElseThrow();

        assertTrue(rpCompany.warehouseCheck(warehouse1.id(), companyIds.get(0)));
        assertFalse(rpCompany.warehouseCheck(warehouse2.id(), companyIds.get(0)));
    }

    @Test
    void checkUserTest() throws SQLException {
        RpCompany rpCompany = new RpCompany(dataSource);
        List<UUID> companyIds = getCompanies(2, false).stream().map(Company::id).toList();

        RpUser rpUser = new RpUser(dataSource, new BCryptPasswordEncoder());
        User user1 = rpUser.add(generateUser(), companyIds.get(0)).orElseThrow();
        User user2 = rpUser.add(generateUser(), companyIds.get(1)).orElseThrow();

        assertTrue(rpCompany.userCheck(user1.id(), companyIds.get(0)));
        assertFalse(rpCompany.userCheck(user2.id(), companyIds.get(0)));
    }

}
