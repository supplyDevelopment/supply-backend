package supply.server.data.subscribe;

public record Subscribe(
    String id,
    String status,
    String description,
    Confirmation confirmation
) {
    public record Confirmation(
        String type,
        String confirmation_url
    ) {
    }
}
