package ru.supply.data.item.product;

import ru.supply.data.utils.item.ItemStatus;

import java.time.LocalDate;
import java.util.UUID;

public record Product(
    UUID id,
    String name,
    String description,
    int count,
    ItemStatus status,
    LocalDate expirationDate
) {
}
