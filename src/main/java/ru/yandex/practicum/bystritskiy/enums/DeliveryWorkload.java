package ru.yandex.practicum.bystritskiy.enums;

import java.math.BigDecimal;

public enum DeliveryWorkload {
    NORMAL(new BigDecimal("1.00")),
    INCREASED(new BigDecimal("1.20")),
    HIGH(new BigDecimal("1.40")),
    VERY_HIGH(new BigDecimal("1.60"));

    private final BigDecimal coefficient;

    DeliveryWorkload(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }
}
