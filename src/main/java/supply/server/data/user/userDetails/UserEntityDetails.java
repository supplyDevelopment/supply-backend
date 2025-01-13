package supply.server.data.user.userDetails;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import supply.server.data.user.User;

import java.util.Collection;
import java.util.UUID;

@AllArgsConstructor
public class UserEntityDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.permissions();
    }

    @Override
    public String getPassword() {
        return user.password();
    }

    @Override
    public String getUsername() {
        return user.email().toString();
    }

    public UUID getId() {
        return user.id();
    }
}