package ru.yandex.practicum.bystritskiy.enums;

import java.math.BigDecimal;

public enum CargoSize {
    SMALL(new BigDecimal("100.00")),
    LARGE(new BigDecimal("200.00"));

    private final BigDecimal surcharge;

    CargoSize(BigDecimal surcharge) {
        this.surcharge = surcharge;
    }

    public BigDecimal getSurcharge() {
        return surcharge;
    }
}