package supply.server.data.utils.user;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public enum UserPermission implements GrantedAuthority, Serializable {
    READ,
    WRITE,
    DELETE,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
