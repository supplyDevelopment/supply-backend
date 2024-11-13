package ru.supply.data.warehouse;

import ru.supply.data.utils.Address;

import java.time.LocalDate;
import java.util.UUID;

public record Warehouse (
    UUID id,
    String name,
    Address location,
    UUID companyId,
    Long stockLevel,
    Long capacity,
    LocalDate createdAt,
    LocalDate updatedAt
) implements Warehouses {
}
