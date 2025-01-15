package supply.server.service;

import org.springframework.stereotype.Service;
import supply.server.configuration.exception.IncorrectParameterException;
import supply.server.data.project.Project;
import supply.server.data.resource.CreateResource;
import supply.server.data.resource.Resource;
import supply.server.data.user.CreateUser;
import supply.server.data.user.User;
import supply.server.data.warehouse.CreateWarehouse;
import supply.server.data.warehouse.Warehouse;

@Service
public class CreationService extends UserService {

    public CreationService(RepositoryService repository) {
        super(repository);
    }

    public Project createProject(String name, String description) {
        return repository.getProject().add(name, description, user().companyId());
    }

    public Warehouse createWarehouse(CreateWarehouse createWarehouse) {
        if (!validate(createWarehouse)) {
            throw new IncorrectParameterException("Not allowed to create warehouse with this parameters");
        }
        return repository.getWarehouse().add(createWarehouse, user().companyId());
    }

    public User createUser(CreateUser createUser) {
        return repository.getUser().add(createUser, user().companyId());
    }

    public Resource createResource(CreateResource createResource) {
        if (!validate(createResource)) {
            throw new IncorrectParameterException("Not allowed to create resource with this parameters");
        }
        return repository.getResource().add(createResource, user().companyId());
    }

    private boolean validate(CreateResource createResource) {
        return repository.getCompany().projectCheck(createResource.projectId(), user().companyId())
                && repository.getCompany().warehouseCheck(createResource.warehouseId(), user().companyId())
                && repository.getCompany().userCheck(createResource.userId(), user().companyId());
    }

    private boolean validate(CreateWarehouse createWarehouse) {
        return createWarehouse
                .admins()
                .stream()
                .filter(admin -> !repository.getCompany().userCheck(admin, user().companyId()))
                .findFirst().isEmpty();
    }

}
