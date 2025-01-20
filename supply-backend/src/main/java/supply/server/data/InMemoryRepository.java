package supply.server.data;

import org.springframework.stereotype.Service;
import supply.server.data.company.InMemoryRpCompany;
import supply.server.data.project.InMemoryRpProject;
import supply.server.data.resource.InMemoryRpResource;
import supply.server.data.user.InMemoryRpUser;
import supply.server.data.warehouse.InMemoryRpWarehouse;

@Service
public record InMemoryRepository(
        InMemoryRpCompany inMemoryRpCompany,
        InMemoryRpUser inMemoryRpUser,
        InMemoryRpWarehouse inMemoryRpWarehouse,
        InMemoryRpProject inMemoryRpProject,
        InMemoryRpResource inMemoryRpResource
) {
}
