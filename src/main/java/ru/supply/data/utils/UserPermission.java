package ru.supply.data.utils;

import org.springframework.security.core.GrantedAuthority;

public enum UserPermission implements GrantedAuthority {
    READ,
    WRITE,
    DELETE,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
