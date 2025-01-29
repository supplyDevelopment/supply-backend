package supply.server.data.company;

import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Company (
        UUID id,
        String name,
        List<Email> contactEmails,
        List<Phone> contactPhones,
        Bil bil_address,
        Tax tax,
        List<Address> addresses,
        CompanyStatus status,
        LocalDate subscriptionExpiresAt,
        LocalDate createdAt,
        LocalDate updatedAt
) implements Serializable {
}
