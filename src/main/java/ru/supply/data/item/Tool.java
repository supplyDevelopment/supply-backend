package ru.supply.data.item;

import ru.supply.data.utils.item.ToolStatus;

import java.util.UUID;

public record Tool(
    UUID id,
    String name,
    String description,
    ToolStatus status,
    String serialNumber
) {
}
