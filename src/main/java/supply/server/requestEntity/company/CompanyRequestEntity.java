package supply.server.requestEntity.company;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import supply.server.data.utils.Address;
import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;
import supply.server.data.utils.company.Bil;
import supply.server.data.utils.company.CompanyStatus;
import supply.server.data.utils.company.Tax;
import supply.server.requestEntity.deserializer.*;

import java.util.List;

public record CompanyRequestEntity(
    String name,
    @JsonDeserialize(using = EmailDeserializer.class)
    List<Email> contactEmails,
    @JsonDeserialize(using = PhoneDeserializer.class)
    List<Phone> contactPhones,
    @JsonDeserialize(using = BillDeserializer.class)
    Bil bil_address,
    @JsonDeserialize(using = TaxDeserializer.class)
    Tax tax_id,
    @JsonDeserialize(using = AddressDeserializer.class)
    List<Address> addresses,
    @JsonDeserialize(using = CompanyStatusDeserializer.class)
    CompanyStatus status
) {
}
