package supply.server.data.user.userDetails;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import supply.server.data.user.RpUser;
import supply.server.data.user.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@Component
public class UserEntityDetailsService implements UserDetailsService {

    @Autowired
    private DataSource dataSource;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        RpUser rpUser = new RpUser(dataSource);
        Optional<User> user = Optional.empty();
        try {
            user = rpUser.get(email);
        } catch (SQLException e) {
            log.error("Error in loadUserByUsername {}", e.getMessage());
        }
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }
        return new UserEntityDetails(user.get());
    }
}
