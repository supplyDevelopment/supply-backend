package supply.server.data.user;

import java.util.UUID;

public interface Users {

    UUID id();
    String password();
    String email();
    // @TODO: add getters for all fields
}
