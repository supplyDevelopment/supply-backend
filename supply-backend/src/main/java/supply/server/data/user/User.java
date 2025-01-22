package supply.server.data.user;

import supply.server.data.utils.Email;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.Phone;
import supply.server.data.utils.user.UserPermission;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public record User (
        UUID id,
        UserName name,
        Email email,
        Phone phone,
        String password,
        UUID companyId,
        List<UserPermission> permissions,
        LocalDate createdAt,
        LocalDate updatedAt
) implements Serializable {
}
