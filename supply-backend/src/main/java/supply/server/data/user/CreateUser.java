package supply.server.data.user;

import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record CreateUser(
    UserName name,
    Email email,
    Phone phone,
    String password,
    List<UserPermission> permissions
) {
    public CreateUser(
            String firstName,
            String secondName,
            Optional<String> lastName,
            String email,
            String phone,
            Optional<String> password,
            List<UserPermission> permissions) {
        this(
                new UserName(firstName, secondName, lastName.orElse(null)),
                new Email(email),
                new Phone(phone),
                password.orElse(UUID.randomUUID().toString().replace("-", "")),
                permissions
        );
    }
}
