package supply.server.data.item.tool;

import supply.server.data.utils.item.ItemStatus;

import java.util.UUID;

public record Tool(
    UUID id,
    String name,
    String description,
    ItemStatus status,
    String serialNumber
) {
}
