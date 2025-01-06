package supply.server.data.supplier;

import supply.server.data.utils.Email;
import supply.server.data.utils.Phone;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Supplier(
    UUID id,
    String name,
    List<Email> emails,
    List<Phone> phones,
    String address,
    LocalDate createdAt,
    LocalDate updatedAt
) {
}
