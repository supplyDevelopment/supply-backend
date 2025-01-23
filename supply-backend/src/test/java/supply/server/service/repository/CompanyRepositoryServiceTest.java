package supply.server.service.repository;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import supply.server.configuration.DataCreator;
import supply.server.configuration.exception.DataNotFoundException;
import supply.server.data.Redis;
import supply.server.data.company.Company;
import supply.server.data.company.CreateCompany;
import supply.server.data.company.RpCompany;
import supply.server.data.project.Project;
import supply.server.data.project.RpProject;
import supply.server.data.user.RpUser;
import supply.server.data.user.User;
import supply.server.data.warehouse.RpWarehouse;
import supply.server.data.warehouse.Warehouse;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@AllArgsConstructor
public class CompanyRepositoryServiceTest extends DataCreator {

    private final Redis<Company> inMemoryRpCompany = new Redis<>(redisTemplate, "company:");
    private final RpCompany rpCompany = new RpCompany(dataSource);

    private final CompanyRepositoryService companyService = new CompanyRepositoryService(rpCompany, inMemoryRpCompany);

    @Test
    void addAndGetTest() throws SQLException {
        CreateCompany createCompany = generateCompany();

        Company company = companyService.add(createCompany);
        checkEquality(createCompany, company);

        Company actual1 = inMemoryRpCompany.get(company.id()).orElseThrow();
        checkEquality(company, actual1);

        Company actual2 = rpCompany.get(company.id()).orElseThrow();
        checkEquality(company, actual2);

        Company actual3 = companyService.get(company.id());
        checkEquality(company, actual3);
    }

    @Test
    void getRpTest() throws SQLException {
        Company company = getCompany(true);

        assertTrue(inMemoryRpCompany.get(company.id()).isEmpty());

        Company actual = companyService.get(company.id());
        checkEquality(company, actual);

        assertTrue(inMemoryRpCompany.get(company.id()).isPresent());

        assertThrows(DataNotFoundException.class, () -> companyService.get(UUID.randomUUID()));
    }

    @Test
    void checkProjectTest() throws SQLException {
        List<UUID> companyIds = getCompanies(2, false).stream().map(Company::id).toList();

        RpProject rpProject = new RpProject(dataSource);
        Project project1 = rpProject.add("testName", "testDescription", companyIds.get(0)).orElseThrow();
        Project project2 = rpProject.add("testName", "testDescription", companyIds.get(1)).orElseThrow();

        assertTrue(companyService.projectCheck(project1.id(), companyIds.get(0)));
        assertFalse(companyService.projectCheck(project2.id(), companyIds.get(0)));
    }

    @Test
    void checkWarehouseTest() throws SQLException {
        List<UUID> companyIds = getCompanies(2, false).stream().map(Company::id).toList();

        RpWarehouse rpWarehouse = new RpWarehouse(dataSource);
        Warehouse warehouse1 = rpWarehouse.add(generateWarehouse(List.of()), companyIds.get(0)).orElseThrow();
        Warehouse warehouse2 = rpWarehouse.add(generateWarehouse(List.of()), companyIds.get(1)).orElseThrow();

        assertTrue(companyService.warehouseCheck(warehouse1.id(), companyIds.get(0)));
        assertFalse(companyService.warehouseCheck(warehouse2.id(), companyIds.get(0)));
    }

    @Test
    void checkUserTest() throws SQLException {
        List<UUID> companyIds = getCompanies(2, false).stream().map(Company::id).toList();

        RpUser rpUser = new RpUser(dataSource, new BCryptPasswordEncoder());
        User user1 = rpUser.add(generateUser(), companyIds.get(0)).orElseThrow();
        User user2 = rpUser.add(generateUser(), companyIds.get(1)).orElseThrow();

        assertTrue(companyService.userCheck(user1.id(), companyIds.get(0)));
        assertFalse(companyService.userCheck(user2.id(), companyIds.get(0)));
    }

    private void checkEquality(CreateCompany createCompany, Company company) {
        assertEquals(createCompany.name(), company.name());
        assertEquals(createCompany.contactEmails().get(0).getEmail(), company.contactEmails().get(0).getEmail());
        assertEquals(createCompany.contactPhones().get(0).getPhone(), company.contactPhones().get(0).getPhone());
        assertEquals(createCompany.bil_address().getBil(), company.bil_address().getBil());
        assertEquals(createCompany.tax().getTax(), company.tax().getTax());
        assertEquals(createCompany.addresses().get(0).getAddress(), company.addresses().get(0).getAddress());
        assertEquals(createCompany.status(), company.status());
    }

    private void checkEquality(Company expected, Company actual) {
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.contactEmails().get(0).getEmail(), actual.contactEmails().get(0).getEmail());
        assertEquals(expected.contactPhones().get(0).getPhone(), actual.contactPhones().get(0).getPhone());
        assertEquals(expected.bil_address().getBil(), actual.bil_address().getBil());
        assertEquals(expected.tax().getTax(), actual.tax().getTax());
        assertEquals(expected.addresses().get(0).getAddress(), actual.addresses().get(0).getAddress());
        assertEquals(expected.status(), actual.status());
        assertEquals(expected.createdAt(), actual.createdAt());
        assertEquals(expected.updatedAt(), actual.updatedAt());
    }

}
