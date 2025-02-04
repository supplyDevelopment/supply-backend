package supply.server.controller.entity.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import supply.server.data.user.CreateUser;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CreateUserRequest(
        UserNameRequest userName,
        @NotNull(message = "Email cannot be null")
        String email,
        String phone,
        @NotNull
        @NotEmpty
        List<UserPermission> permissions
) {
    public record UserNameRequest(
            @NotNull
            @NotEmpty
            String firstName,
            @NotNull
            String secondName,
            String lastName
    ) {
        public UserName toUserName() {
            return new UserName(firstName, secondName, lastName);
        }
    }

    public CreateUser toCreateUser() {
        return new CreateUser(
                Objects.isNull(userName) ? null : userName.toUserName(),
                new Email(email),
                Objects.isNull(phone) ? null : new Phone(phone),
                UUID.randomUUID().toString().replace("-", ""),
                permissions
        );
    }
}
