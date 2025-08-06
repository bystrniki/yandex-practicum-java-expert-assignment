package ru.yandex.practicum.bystritskiy;

import ru.yandex.practicum.bystritskiy.enums.CargoSize;
import ru.yandex.practicum.bystritskiy.enums.DeliveryWorkload;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class DeliveryCalculator {

    private static final BigDecimal MIN_DELIVERY_COST = new BigDecimal("400.00");
    private static final BigDecimal FRAGILE_SURCHARGE = new BigDecimal("300.00");
    private static final BigDecimal MAX_FRAGILE_DISTANCE = new BigDecimal("30.00");

    private static final BigDecimal DISTANCE_THRESHOLD_2KM = new BigDecimal("2.00");
    private static final BigDecimal DISTANCE_THRESHOLD_10KM = new BigDecimal("10.00");
    private static final BigDecimal DISTANCE_THRESHOLD_30KM = new BigDecimal("30.00");

    private static final BigDecimal COST_UP_TO_2KM = new BigDecimal("50.00");
    private static final BigDecimal COST_UP_TO_10KM = new BigDecimal("100.00");
    private static final BigDecimal COST_UP_TO_30KM = new BigDecimal("200.00");
    private static final BigDecimal COST_OVER_30KM = new BigDecimal("300.00");

    private static final int SCALE = 2;

    public BigDecimal calculateDeliveryCost(BigDecimal distance, CargoSize cargoSize, boolean isFragile, DeliveryWorkload deliveryWorkload) {
        validateInput(distance, cargoSize, deliveryWorkload, isFragile);

        BigDecimal baseCost = calculateDistanceCost(distance);
        baseCost = baseCost.add(cargoSize.getSurcharge());

        if (isFragile) {
            baseCost = baseCost.add(FRAGILE_SURCHARGE);
        }

        BigDecimal finalCost = baseCost.multiply(deliveryWorkload.getCoefficient());

        return finalCost.max(MIN_DELIVERY_COST).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private void validateInput(BigDecimal distance, CargoSize cargoSize, DeliveryWorkload deliveryWorkload, boolean isFragile) {
        Objects.requireNonNull(distance, "Расстояние не может быть null");
        Objects.requireNonNull(cargoSize, "Размер груза не может быть null");
        Objects.requireNonNull(deliveryWorkload, "Загруженность не может быть null");

        if (distance.signum() <= 0) {
            throw new IllegalArgumentException("Расстояние должно быть больше 0 км");
        }

        if (isFragile && distance.compareTo(MAX_FRAGILE_DISTANCE) > 0) {
            throw new IllegalArgumentException("Хрупкие грузы нельзя возить на расстояние более 30 км");
        }
    }

    private BigDecimal calculateDistanceCost(BigDecimal distance) {
        if (distance.compareTo(DISTANCE_THRESHOLD_30KM) > 0) {
            return COST_OVER_30KM;
        } else if (distance.compareTo(DISTANCE_THRESHOLD_10KM) > 0) {
            return COST_UP_TO_30KM;
        } else if (distance.compareTo(DISTANCE_THRESHOLD_2KM) > 0) {
            return COST_UP_TO_10KM;
        } else {
            return COST_UP_TO_2KM;
        }
    }
}
