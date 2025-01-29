package supply.server.redis;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import supply.server.configuration.redis.RedisConnection;
import supply.server.data.Redis;
import supply.server.data.company.Company;
import supply.server.data.project.Project;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RedisCacheTest extends RedisConnection {

    Redis<Company> rpCompany = new Redis<>(redisTemplate, "company:");
    Redis<Project> rpProject = new Redis<>(redisTemplate, "project:");

    @Test
    void addAndGet() {
        UUID id = UUID.randomUUID();
        Company company = new Company(
                id,
                "testCompany",
                List.of(new Email("example@example.com")),
                List.of(new Phone("+71234567890")),
                new Bil("2234567890"),
                new Tax("2234567890"),
                List.of(new Address("test")),
                CompanyStatus.ACTIVE,
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now()
        );
        Optional<Company> result = rpCompany.set(company.id(), company);
        assertTrue(result.isPresent());

        Optional<Company> result2 = rpCompany.get(company.id());
        assertTrue(result2.isPresent());
        assertEquals(company.id(), result2.get().id());
        assertEquals(company.name(), result2.get().name());
        assertEquals(company.contactEmails().stream().map(Email::getEmail).toList(), result2.get().contactEmails().stream().map(Email::getEmail).toList());
        assertEquals(company.contactPhones().stream().map(Phone::getPhone).toList(), result2.get().contactPhones().stream().map(Phone::getPhone).toList());
        assertEquals(company.bil_address().getBil(), result2.get().bil_address().getBil());
        assertEquals(company.tax().getTax(), result2.get().tax().getTax());
        assertEquals(company.addresses().stream().map(Address::getAddress).toList(), result2.get().addresses().stream().map(Address::getAddress).toList());
        assertEquals(company.status(), result2.get().status());
        assertEquals(company.createdAt(), result2.get().createdAt());
        assertEquals(company.updatedAt(), result2.get().updatedAt());

        Project project = new Project(
                id,
                "name",
                "description",
                id,
                LocalDate.now(),
                LocalDate.now()
        );
        rpProject.set(id, project);
        Optional<Project> getProject = rpProject.get(id);
        assertTrue(getProject.isPresent());
        assertEquals(project.id(), getProject.get().id());
        assertEquals(project.name(), getProject.get().name());
        assertEquals(project.description(), getProject.get().description());
        assertEquals(project.companyId(), getProject.get().companyId());
        assertEquals(project.createdAt(), getProject.get().createdAt());
        assertEquals(project.updatedAt(), getProject.get().updatedAt());
    }

    @Disabled
    @Test
    @Disabled
    void syncAccessAddAndGetTest() throws InterruptedException {
        UUID id = UUID.randomUUID();
        Company company1 = new Company(
                id,
                "testCompany1",
                List.of(new Email("example@example.com")),
                List.of(new Phone("+71234567890")),
                new Bil("2234567890"),
                new Tax("2234567890"),
                List.of(new Address("test")),
                CompanyStatus.ACTIVE,
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now()
        );
        Company company2 = new Company(
                id,
                "testCompany2",
                List.of(new Email("example@example.com")),
                List.of(new Phone("+71234567890")),
                new Bil("2234567890"),
                new Tax("2234567890"),
                List.of(new Address("test")),
                CompanyStatus.ACTIVE,
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now()
        );
        Project project = new Project(
                id,
                "name",
                "description",
                id,
                LocalDate.now(),
                LocalDate.now()
        );

        Thread thread1 = new Thread(() -> rpCompany.set(company1.id(), company1));

        Thread thread2 = new Thread(() -> {
            rpProject.set(project.id(), project);
        });

        Thread thread3 = new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            rpCompany.set(company2.id(), company2);
        });

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();

        // Добавляем проверки с логированием
        Optional<Project> projectResult = rpProject.get(project.id());
        if (projectResult.isEmpty()) {
            fail("Project was not found in Redis");
        } else {
            assertEquals(project.name(), projectResult.get().name());
        }

        Optional<Company> result = rpCompany.get(company1.id());
        if (result.isPresent()) {
            assertEquals(company1.id(), result.get().id());
            assertEquals(company1.name(), result.get().name());
        } else {
            fail("Company was not found in Redis");
        }
    }


}
