package ru.supply.requestEntity.company;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ru.supply.data.utils.Address;
import ru.supply.data.utils.Email;
import ru.supply.data.utils.Phone;
import ru.supply.data.utils.company.Bil;
import ru.supply.data.utils.company.CompanyStatus;
import ru.supply.data.utils.company.Tax;
import ru.supply.requestEntity.deserializer.*;

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
