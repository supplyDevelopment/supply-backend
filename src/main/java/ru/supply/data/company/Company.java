package ru.supply.data.company;

import ru.supply.data.utils.Address;
import ru.supply.data.utils.company.Bil;
import ru.supply.data.utils.Email;
import ru.supply.data.utils.Phone;
import ru.supply.data.utils.company.CompanyStatus;
import ru.supply.data.utils.company.Tax;

import java.util.List;
import java.util.UUID;

public record Company (
        UUID id,
        String name,
        UUID admin_id,
        List<Email> contactEmails,
        List<Phone> contactPhones,
        Bil bil_address,
        Tax tax_id,
        List<Address> addresses,
        CompanyStatus status
) {
}
