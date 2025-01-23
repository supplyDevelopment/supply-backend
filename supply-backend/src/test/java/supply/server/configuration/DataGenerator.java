package supply.server.configuration;

import supply.server.configuration.postres.DBConnection;
import supply.server.data.company.CreateCompany;
import supply.server.data.resource.CreateResource;
import supply.server.data.resource.types.ResourceStatus;
import supply.server.data.resource.types.ResourceType;
import supply.server.data.user.CreateUser;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.Unit;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;
import supply.server.data.warehouse.CreateWarehouse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DataGenerator extends RedisAndDBConnection {

    private final StringGenerator stringGenerator = new StringGenerator();

    protected String generateString(int length) {
        return stringGenerator.generate(length, true, true);
    }

    protected CreateCompany generateCompany() {
        return new CreateCompany(
                stringGenerator.generate(10, true, true),
                List.of(new Email(stringGenerator.generate(2, true, true) + "@gmail.com")),
                List.of(new Phone("+7" + stringGenerator.generate(10, false, true))),
                new Bil(stringGenerator.generate(10, false, true)),
                new Tax(stringGenerator.generate(10, false, true)),
                List.of(new Address(stringGenerator.generate(10, true, true))),
                CompanyStatus.ACTIVE
        );
    }

    protected CreateUser generateUser() {
        return new CreateUser(
                new UserName(
                        stringGenerator.generate(10, true, false),
                        stringGenerator.generate(10, true, false),
                        stringGenerator.generate(10, true, false)
                ),
                new Email(stringGenerator.generate(2, true, true) + "@gmail.com"),
                new Phone("+7" + stringGenerator.generate(10, false, true)),
                stringGenerator.generate(10, true, true),
                List.of(UserPermission.DELETE)
        );
    }

    protected CreateWarehouse generateWarehouse(List<UUID> userIds) {
        return new CreateWarehouse(
                stringGenerator.generate(10, true, true),
                new Address(stringGenerator.generate(10, true, true)),
                0L,
                0L,
                userIds
        );
    }

    protected CreateResource generateResource(UUID userId, UUID warehouseId, UUID projectId) {
        URL url;
        try {
            url = new URL("http://" + stringGenerator.generate(10, true, true) + ".com");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return new CreateResource(
                List.of(url),
                stringGenerator.generate(10, true, true),
                1,
                Unit.KG,
                ResourceType.PRODUCT,
                userId,
                warehouseId,
                projectId,
                ResourceStatus.ACTIVE,
                stringGenerator.generate(10, true, true)
        );
    }

}
