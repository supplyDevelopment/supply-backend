package supply.server.data.company;

import com.jcabi.jdbc.JdbcSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RpCompanyTest extends DataCreator {

    private final DataSource dataSource = dataSource();

    @Test
    void addTest() throws Exception {
        RpCompany rpCompany = new RpCompany(dataSource);

        CreateCompany createCompany = generateCompany();
        Company company = rpCompany.add(createCompany).orElseThrow();

        Assertions.assertEquals(createCompany.name(), company.name());
        Assertions.assertEquals(createCompany.contactEmails().get(0).getEmail(), company.contactEmails().get(0).getEmail());
        Assertions.assertEquals(createCompany.contactPhones().get(0).getPhone(), company.contactPhones().get(0).getPhone());
        Assertions.assertEquals(createCompany.bil_address().getBil(), company.bil_address().getBil());
        Assertions.assertEquals(createCompany.tax().getTax(), company.tax().getTax());
        Assertions.assertEquals(createCompany.addresses().get(0).getAddress(), company.addresses().get(0).getAddress());
        Assertions.assertEquals(createCompany.status(), company.status());

        JdbcSession jdbcSession = new JdbcSession(dataSource);
        Company actual = jdbcSession
                .sql("""
                        SELECT id, name, contact_emails, contact_phones,
                         bil_address, tax, addresses, status,
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
                                rset.getDate("created_at").toLocalDate(),
                                rset.getDate("updated_at").toLocalDate(),
                                dataSource
                        ));
                    }
                    return Optional.<Company>empty();
                }).orElseThrow();

        Assertions.assertEquals(actual.id(), company.id());
        Assertions.assertEquals(actual.name(), company.name());
        Assertions.assertEquals(actual.contactEmails().get(0).getEmail(), company.contactEmails().get(0).getEmail());
        Assertions.assertEquals(actual.contactPhones().get(0).getPhone(), company.contactPhones().get(0).getPhone());
        Assertions.assertEquals(actual.bil_address().getBil(), company.bil_address().getBil());
        Assertions.assertEquals(actual.tax().getTax(), company.tax().getTax());
        Assertions.assertEquals(actual.addresses().get(0).getAddress(), company.addresses().get(0).getAddress());
        Assertions.assertEquals(actual.status(), company.status());
        Assertions.assertEquals(actual.createdAt(), company.createdAt());
        Assertions.assertEquals(actual.updatedAt(), company.updatedAt());
    }

    @Test
    void getTest() throws Exception {
        RpCompany rpCompany = new RpCompany(dataSource);
        Company expected = getCompany(false);
        Company actual = rpCompany.get(expected.id()).orElseThrow();

        Assertions.assertEquals(expected.id(), actual.id());
        Assertions.assertEquals(expected.name(), actual.name());
        Assertions.assertEquals(expected.contactEmails().get(0).getEmail(), actual.contactEmails().get(0).getEmail());
        Assertions.assertEquals(expected.contactPhones().get(0).getPhone(), actual.contactPhones().get(0).getPhone());
        Assertions.assertEquals(expected.bil_address().getBil(), actual.bil_address().getBil());
        Assertions.assertEquals(expected.tax().getTax(), actual.tax().getTax());
        Assertions.assertEquals(expected.addresses().get(0).getAddress(), actual.addresses().get(0).getAddress());
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

        RpUser rpUser = new RpUser(dataSource);
        User user1 = rpUser.add(generateUser(), companyIds.get(0)).orElseThrow();
        User user2 = rpUser.add(generateUser(), companyIds.get(1)).orElseThrow();

        assertTrue(rpCompany.userCheck(user1.id(), companyIds.get(0)));
        assertFalse(rpCompany.userCheck(user2.id(), companyIds.get(0)));
    }

}
