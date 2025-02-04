package supply.server.service.dataService;

import org.springframework.stereotype.Service;
import supply.server.data.company.Company;
import supply.server.data.user.User;
import supply.server.data.utils.Email;

import java.util.List;
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

    public void removeUser(UUID id) {
        repository.getUser().delete(id, user().companyId());
    }

}
