package supply.server.service.dataService;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import supply.server.configuration.exception.IncorrectParameterException;
import supply.server.data.company.Company;
import supply.server.data.resource.CreateResource;
import supply.server.data.resource.EditResource;
import supply.server.data.resource.Resource;
import supply.server.data.resource.history.CreateHistory;
import supply.server.data.resource.history.ResourceHistory;
import supply.server.data.user.User;
import supply.server.data.utils.Email;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UpdateService extends UserService {

    public UpdateService(RepositoryService repository) {
        super(repository);
    }

    public Company updateCompany(List<Email> emails) {
        return repository.getCompany().update(user().companyId(), emails);
    }

    public User updateUser(Email email) {
        return repository.getUser().update(user().id(), email, user().companyId());
    }

    public User updateUser(String password) {
        return repository.getUser().updatePassword(user().id(), password, user().companyId());
    }

    public Pair<Resource, Resource> updateResource(UUID resourceId, EditResource editResource) {
        Resource updatableResource = repository.getResource().get(resourceId, user().companyId());

        if (updatableResource.count() < editResource.quantity()) {
            throw new IncorrectParameterException("Resource does not have enough quantity");
        }

        CreateResource createUpdatedResource = editResource.edit(updatableResource);

        Resource updatedResource;
        Optional<Resource> updatedResourceOpt = repository.getResource().get(createUpdatedResource, user().companyId());
        if (updatedResourceOpt.isEmpty()) {
            updatedResource = repository.getResource().add(createUpdatedResource, user().companyId());
        } else {
            updatedResource = updatedResourceOpt.get();
            updatedResource = repository.getResource().edit(updatedResource.id(), user().companyId(), updatedResource.count() + createUpdatedResource.count());
        }

        updatableResource = repository.getResource().edit(resourceId, user().companyId(), updatableResource.count() - editResource.quantity());

        repository.getHistory().add(
                new CreateHistory(editResource, updatableResource, updatedResource),
                user().companyId()
        );


        return Pair.of(updatableResource, updatedResource);
    }

    public void expendResource(UUID resourceId, int quantity) {
        Resource resource = repository.getResource().get(resourceId, user().companyId());

        if (resource.count() < quantity) {
            throw new IncorrectParameterException("Resource does not have enough quantity");
        }

        repository.getHistory().add(
            CreateHistory.expend(resource, quantity),
            user().companyId()
        );

        if (resource.count() == quantity) {
            repository.getResource().delete(resourceId, user().companyId());
        } else {
            repository.getResource().edit(resourceId, user().companyId(), resource.count() - quantity);
        }
    }

    public void removeUser(UUID id) {
        repository.getUser().delete(id, user().companyId());
    }

}
