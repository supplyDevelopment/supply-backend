package supply.server.data.subscribe;

import java.util.UUID;

public record Subscribe(
    UUID id,
    String status,
    String description,
    Confirmation confirmation
) {
    record Confirmation(
        String type,
        String confirmation_url
    ) {
    }
}
