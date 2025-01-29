package supply.server.service.dataService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import supply.server.configuration.exception.AuthenticationException;
import supply.server.data.company.Company;
import supply.server.data.company.CreateCompany;
import supply.server.data.user.CreateUser;
import supply.server.data.utils.Email;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.user.UserPermission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CompanyService {

    private final RepositoryService repository;
    private final AuthenticationService authenticationService;
    private final CreationService creationService;

    public String createCompany(Email admin) {
        CreateCompany createCompany = new CreateCompany(
                null,
                List.of(admin),
                null,
                null,
                null,
                null,
                CompanyStatus.ACTIVE
        );
        Company company = repository.getCompany().add(createCompany);

        String password = UUID.randomUUID().toString().replace("-", "");
        CreateUser createUser = new CreateUser(
                null,
                admin,
                null,
                password,
                List.of(UserPermission.ADMIN)
        );
        creationService.createUser(createUser);

        Optional<String> jwt = authenticationService.authenticate(admin, password);
        if (jwt.isPresent()) {
            return jwt.get();
        } else {
            throw new AuthenticationException("Failed to authenticate user");
        }
    }



}
