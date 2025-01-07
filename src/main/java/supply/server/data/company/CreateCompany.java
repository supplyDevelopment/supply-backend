package supply.server.data.company;

import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;

import java.util.List;

public record CreateCompany(
        String name,
        List<Email> contactEmails,
        List<Phone> contactPhones,
        Bil bil_address,
        Tax tax,
        List<Address> addresses,
        CompanyStatus status
) {
}
