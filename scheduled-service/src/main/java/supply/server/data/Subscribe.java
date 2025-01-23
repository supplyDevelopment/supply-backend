package supply.server.data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Subscribe(
        UUID companyId,
        List<String> contactEmails,
        LocalDate expirationDate
) {
}
