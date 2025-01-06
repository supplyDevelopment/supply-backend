package supply.server.requestEntity.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.user.UserName;
import supply.server.data.utils.user.UserPermission;
import supply.server.requestEntity.deserializer.EmailDeserializer;
import supply.server.requestEntity.deserializer.PhoneDeserializer;
import supply.server.requestEntity.deserializer.UserNameDeserializer;
import supply.server.requestEntity.deserializer.UserPermissionDeserializer;

import java.util.List;
import java.util.UUID;

public record UserRequestEntity (
        @JsonDeserialize(using = UserNameDeserializer.class)
        UserName name,
        @JsonDeserialize(using = EmailDeserializer.class)
        Email email,
        @JsonDeserialize(using = PhoneDeserializer.class)
        Phone phone,
        @NotNull
        @Size(min = 6, message = "Password should be at least 6 characters long")
        String password,
        @NotNull
        UUID companyId,
        @JsonDeserialize(using = UserPermissionDeserializer.class)
        List<UserPermission> permissions
) {
}
