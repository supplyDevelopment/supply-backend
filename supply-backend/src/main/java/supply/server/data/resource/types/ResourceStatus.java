package supply.server.data.resource.types;

import supply.server.configuration.exception.IncorrectInputException;
import java.io.Serializable;

public enum ResourceStatus implements Serializable {
    ACTIVE,
    INACTIVE,
    REPAIR,
    DELIVER,
    EMPTY;

    public static ResourceStatus fromString(String name) {
        for (ResourceStatus status : ResourceStatus.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IncorrectInputException("Unknown resource status: " + name);
    }
}

