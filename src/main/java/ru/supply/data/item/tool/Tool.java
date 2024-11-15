package ru.supply.data.item.tool;

import ru.supply.data.utils.item.ItemStatus;

import java.util.UUID;

public record Tool(
    UUID id,
    String name,
    String description,
    ItemStatus status,
    String serialNumber
) {
}
