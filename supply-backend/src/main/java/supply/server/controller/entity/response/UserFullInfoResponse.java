package supply.server.controller.entity.response;

import supply.server.data.user.User;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserFullInfoResponse(
        UUID id,
        UserName name,
        Email email,
        Phone phone,
        UUID companyId,
        List<UserPermission> permissions,
        LocalDate createdAt,
        LocalDate updatedAt
) {
    public UserFullInfoResponse(User user) {
        this(
                user.id(),
                user.name(),
                user.email(),
                user.phone(),
                user.companyId(),
                user.permissions(),
                user.createdAt(),
                user.updatedAt()
        );
    }
}
