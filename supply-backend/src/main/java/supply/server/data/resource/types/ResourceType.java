package supply.server.data.resource.types;

import supply.server.configuration.exception.IncorrectParameterException;

import java.io.Serializable;

public enum ResourceType implements Serializable {
    TOOL,
    PRODUCT;

    public static ResourceType fromString(String name) {
        for (ResourceType type : ResourceType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IncorrectParameterException("Unknown resource type: " + name);
    }
}

