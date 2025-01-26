package supply.server.data.company;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record Subscribe(
        UUID companyId,
        String lastPaymentId,
        LocalDate expiresAt
) {
}
