package ru.supply.data.user;

import ru.supply.data.utils.Email;
import ru.supply.data.utils.user.UserName;
import ru.supply.data.utils.Phone;
import ru.supply.data.utils.user.UserPermission;

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
        LocalDate updatedAt,
        List<UUID> warehouses
        )
{
}
