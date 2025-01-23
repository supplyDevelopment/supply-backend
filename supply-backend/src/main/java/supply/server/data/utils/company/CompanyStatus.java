package supply.server.data.utils.company;

import java.io.Serializable;

public enum CompanyStatus implements Serializable {
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
