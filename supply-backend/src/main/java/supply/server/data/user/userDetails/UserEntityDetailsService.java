package supply.server.data.user.userDetails;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import supply.server.service.dataService.RepositoryService;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class UserEntityDetailsService implements UserDetailsService {

    private final RepositoryService repositoryService;

    public UserDetails loadUserById(UUID userId, UUID companyId) throws UsernameNotFoundException {
        return new UserEntityDetails(repositoryService.getUser().get(userId, companyId));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new UserEntityDetails(repositoryService.getUser().get(email));
    }
}
