package ru.supply.requestEntity.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.supply.data.utils.Email;
import ru.supply.data.utils.Phone;
import ru.supply.data.utils.user.UserName;
import ru.supply.data.utils.user.UserPermission;
import ru.supply.requestEntity.deserializer.EmailDeserializer;
import ru.supply.requestEntity.deserializer.PhoneDeserializer;
import ru.supply.requestEntity.deserializer.UserNameDeserializer;
import ru.supply.requestEntity.deserializer.UserPermissionDeserializer;

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
