package supply.server.controller.entity.response;

import supply.server.data.user.User;
import supply.server.data.utils.Email;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserPartialInfoResponse(
        UUID id,
        UserName name,
        Email email,
        UUID companyId,
        LocalDate subscription_end_date,
        List<UserPermission> permissions
) {
    public UserPartialInfoResponse(User user, LocalDate subscription_end_date) {
        this(
                user.id(),
                user.name(),
                user.email(),
                user.companyId(),
                subscription_end_date,
                user.permissions()
        );
    }
}
