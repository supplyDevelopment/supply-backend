package supply.server.data.user;

import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;

import java.util.List;
import java.util.UUID;

public record CreateUser(
    UserName name,
    Email email,
    Phone phone,
    String password,
    UUID companyId,
    List<UserPermission> permissions
) {
}
