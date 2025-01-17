package supply.server.service.dataService;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import supply.server.data.user.User;
import supply.server.data.user.userDetails.UserEntityDetails;

@Service
@AllArgsConstructor
public class UserService {

    protected final RepositoryService repository;

    protected User user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        UserEntityDetails userEntityDetails = (UserEntityDetails) authentication.getPrincipal();

        return repository.getUser().get(userEntityDetails.getId(), userEntityDetails.getCompanyId());
    }

    // TODO: add checks for permissions
    // TODO: add validation for parameters and implement base methods -

}
