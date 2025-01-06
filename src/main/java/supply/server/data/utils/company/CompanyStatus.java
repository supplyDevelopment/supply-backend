package supply.server.data.utils.company;

public enum CompanyStatus {
    ACTIVE,
    INACTIVE;

    public static CompanyStatus of(String status) {
        return switch (status.toLowerCase().trim()) {
            case "active" -> ACTIVE;
            case "inactive" -> INACTIVE;
            default -> null;
        };
    }

}
