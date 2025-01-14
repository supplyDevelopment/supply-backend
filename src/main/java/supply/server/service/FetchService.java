package supply.server.service;

import org.springframework.stereotype.Service;
import supply.server.data.project.Project;
import supply.server.data.resource.Resource;
import supply.server.data.user.User;
import supply.server.data.warehouse.Warehouse;

import java.util.UUID;

@Service
public class FetchService extends UserService {

    public FetchService(RepositoryService repository) {
        super(repository);
    }

    public Project getProject(UUID projectId) {
        return repository.getProject().get(projectId, user().companyId());
    }

    public Warehouse getWarehouse(UUID warehouseId) {
        return repository.getWarehouse().get(warehouseId, user().companyId());
    }

    public User getUser(UUID userId) {
        return repository.getUser().get(userId, user().companyId());
    }

    public Resource getResource(UUID resourceId) {
        return repository.getResource().get(resourceId, user().companyId());
    }

}
