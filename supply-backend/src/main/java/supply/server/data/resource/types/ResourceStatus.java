package supply.server.data.resource.types;

import supply.server.configuration.exception.IncorrectParameterException;

public enum ResourceStatus {
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
        throw new IncorrectParameterException("Unknown resource status: " + name);
    }
}

