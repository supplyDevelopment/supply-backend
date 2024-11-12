package ru.supply.data.utils.company;

import lombok.Getter;

@Getter
public class Tax {
    private final String tax;

    public Tax(String tax) {
        this.tax = tax;
    }
}
