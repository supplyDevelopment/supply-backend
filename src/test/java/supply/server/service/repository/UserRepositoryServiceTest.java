package supply.server.service.repository;

import org.junit.jupiter.api.Test;
import supply.server.configuration.DataCreator;
import supply.server.configuration.exception.DataNotFound;
import supply.server.data.user.CreateUser;
import supply.server.data.user.InMemoryRpUser;
import supply.server.data.user.RpUser;
import supply.server.data.user.User;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryServiceTest extends DataCreator {

    private final InMemoryRpUser inMemoryRpUser = new InMemoryRpUser();
    private final RpUser rpUser = new RpUser(dataSource);

    private final UserRepositoryService userService = new UserRepositoryService(rpUser, inMemoryRpUser);

    @Test
    void addAndGetTest() throws SQLException {
        CreateUser createUser = generateUser();
        UUID companyId = getCompany(true).id();

        User user = userService.add(createUser, companyId);
        checkEquality(createUser, user);

        User actual1 = userService.get(user.id(), companyId);
        checkEquality(user, actual1);

        User actual2 = rpUser.get(user.email().getEmail()).orElseThrow();
        checkEquality(user, actual2);

        User actual3 = rpUser.get(user.id(), companyId).orElseThrow();
        checkEquality(user, actual3);

        User actual4 = inMemoryRpUser.get(user.id(), companyId).orElseThrow();
        checkEquality(user, actual4);
    }

    @Test
    void getRpTest() throws SQLException {
        CreateUser createUser = generateUser();
        UUID companyId = getCompany(true).id();

        User user = rpUser.add(createUser, companyId).orElseThrow();
        checkEquality(createUser, user);

        assertTrue(inMemoryRpUser.get(user.id(), companyId).isEmpty());

        User actual = userService.get(user.id(), companyId);
        checkEquality(user, actual);

        assertTrue(inMemoryRpUser.get(user.id(), companyId).isPresent());

        assertThrows(DataNotFound.class, () -> userService.get(UUID.randomUUID(), companyId));
    }

    private void checkEquality(CreateUser createUser, User user) {
        assertEquals(createUser.name().getFirstName(), user.name().getFirstName());
        assertEquals(createUser.name().getSecondName(), user.name().getSecondName());
        assertEquals(createUser.name().getLastName().orElse(null), user.name().getLastName().orElse(null));
        assertEquals(createUser.email().getEmail(), user.email().getEmail());
        assertEquals(createUser.phone().getPhone(), user.phone().getPhone());
        assertEquals(createUser.password(), user.password());
    }

    private void checkEquality(User expected, User actual) {
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.name().getFirstName(), actual.name().getFirstName());
        assertEquals(expected.name().getSecondName(), actual.name().getSecondName());
        assertEquals(expected.name().getLastName().orElse(null), actual.name().getLastName().orElse(null));
        assertEquals(expected.email().getEmail(), actual.email().getEmail());
        assertEquals(expected.phone().getPhone(), actual.phone().getPhone());
        assertEquals(expected.password(), actual.password());
        assertEquals(expected.companyId(), actual.companyId());
        assertEquals(expected.createdAt(), actual.createdAt());
        assertEquals(expected.updatedAt(), actual.updatedAt());
    }

}
