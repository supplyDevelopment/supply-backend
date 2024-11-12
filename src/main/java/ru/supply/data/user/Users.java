package ru.supply.data.user;

import java.util.UUID;

public interface Users {

    UUID id();
    String password();
    String email();

}
