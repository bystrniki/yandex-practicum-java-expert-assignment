package ru.yandex.practicum.bystritskiy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.math.BigDecimal;
import ru.yandex.practicum.bystritskiy.enums.CargoSize;
import ru.yandex.practicum.bystritskiy.enums.DeliveryWorkload;

@Feature("Калькулятор стоимости доставки")
@DisplayName("Тесты калькулятора стоимости доставки")
class DeliveryCalculatorTest {

    private static DeliveryCalculator calculator;

    @BeforeAll
    static void setUp() {
        calculator = new DeliveryCalculator();
    }

    @Nested
    @Story("Расчёт стоимости по расстоянию")
    @DisplayName("Тесты расчёта стоимости по расстоянию")
    class DistanceBasedCostTests {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Проверка расчёта для расстояния до 2 км")
        @DisplayName("Расстояние до 2 км - базовая стоимость 50 рублей")
        void shouldCalculateCostForDistanceUpTo2Km() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("2"),
                    CargoSize.SMALL,
                    false,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("400.00"), cost);
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Проверка расчёта для расстояния до 10 км")
        @DisplayName("Расстояние до 10 км - базовая стоимость 100 рублей")
        void shouldCalculateCostForDistanceUpTo10Km() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("10.00"),
                    CargoSize.SMALL,
                    false,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("400.00"), cost);
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Проверка расчёта для расстояния более 30 км")
        @DisplayName("Расстояние более 30 км - базовая стоимость 300 рублей")
        void shouldCalculateCostForDistanceOver30Km() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("50.00"),
                    CargoSize.LARGE,
                    false,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("500.00"), cost);
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.01", "1.00", "2.00"})
        @Severity(SeverityLevel.NORMAL)
        @Description("Граничные значения для расстояния до 2 км")
        @DisplayName("Граничные значения расстояния до 2 км")
        void shouldCalculateCorrectlyForBoundaryDistancesUpTo2Km(String distance) {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal(distance),
                    CargoSize.SMALL,
                    false,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("400.00"), cost);
        }

        @ParameterizedTest
        @ValueSource(strings = {"2.01", "5.00", "10.00"})
        @Severity(SeverityLevel.NORMAL)
        @Description("Граничные значения для расстояния до 10 км")
        @DisplayName("Граничные значения расстояния до 10 км")
        void shouldCalculateCorrectlyForBoundaryDistancesUpTo10Km(
                String distance
        ) {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal(distance),
                    CargoSize.SMALL,
                    false,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("400.00"), cost);
        }

        @ParameterizedTest
        @ValueSource(strings = {"11.00", "20.00", "30.00"})
        @Severity(SeverityLevel.NORMAL)
        @Description("Граничные значения для расстояния до 30 км")
        @DisplayName("Граничные значения расстояния до 30 км")
        void shouldCalculateCorrectlyForBoundaryDistancesUpTo30Km(
                String distance
        ) {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal(distance),
                    CargoSize.SMALL,
                    false,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("400.00"), cost);
        }

        @ParameterizedTest
        @ValueSource(strings = {"31.00", "50.00", "100.00"})
        @Severity(SeverityLevel.NORMAL)
        @Description("Граничные значения для расстояния более 30 км")
        @DisplayName("Граничные значения расстояния более 30 км")
        void shouldCalculateCorrectlyForBoundaryDistancesOver30Km(
                String distance
        ) {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal(distance),
                    CargoSize.SMALL,
                    false,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("400.00"), cost);
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @Description("Граничное значение для хрупкого груза")
        @DisplayName(
                "Хрупкий груз на расстоянии граничного значения"
        )
        void shouldThrowForFragileCargoAtDistanceJustOverLimit() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () ->
                            calculator.calculateDeliveryCost(
                                    new BigDecimal("30.01"),
                                    CargoSize.SMALL,
                                    true,
                                    DeliveryWorkload.NORMAL
                            )
            );
            assertEquals(
                    "Хрупкие грузы нельзя возить на расстояние более 30 км",
                    exception.getMessage()
            );
        }
    }

    @Nested
    @Story("Влияние габаритов груза")
    @DisplayName("Тесты влияния габаритов груза на стоимость")
    class CargoSizeTests {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Проверка доплаты за малый груз")
        @DisplayName("Малый груз добавляет 100 рублей")
        void shouldAddSmallCargoSurcharge() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("50.00"),
                    CargoSize.SMALL,
                    false,
                    DeliveryWorkload.HIGH
            );
            assertEquals(new BigDecimal("560.00"), cost);
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Проверка доплаты за большой груз")
        @DisplayName("Большой груз добавляет 200 рублей")
        void shouldAddLargeCargoSurcharge() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("50.00"),
                    CargoSize.LARGE,
                    false,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("500.00"), cost);
        }
    }

    @Nested
    @Story("Обработка хрупких грузов")
    @DisplayName("Тесты обработки хрупких грузов")
    class FragileCargoTests {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Проверка доплаты за хрупкий груз")
        @DisplayName("Хрупкий груз добавляет 300 рублей")
        void shouldAddFragileSurcharge() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("20.00"),
                    CargoSize.SMALL,
                    true,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("600.00"), cost);
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Хрупкий груз нельзя возить на расстояние более 30 км")
        @DisplayName(
                "Исключение при доставке хрупкого груза на расстояние > 30 км"
        )
        void shouldThrowExceptionForFragileCargoOverMaxDistance() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () ->
                            calculator.calculateDeliveryCost(
                                    new BigDecimal("31.00"),
                                    CargoSize.SMALL,
                                    true,
                                    DeliveryWorkload.NORMAL
                            )
            );
            assertEquals(
                    "Хрупкие грузы нельзя возить на расстояние более 30 км",
                    exception.getMessage()
            );
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @Description("Хрупкий груз можно возить на расстояние ровно 30 км")
        @DisplayName("Доставка хрупкого груза на расстояние 30 км разрешена")
        void shouldAllowFragileCargoAtMaxDistance() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("30.00"),
                    CargoSize.SMALL,
                    true,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("600.00"), cost);
        }
    }

    @Nested
    @Story("Коэффициенты загруженности")
    @DisplayName("Тесты коэффициентов загруженности службы")
    class DeliveryWorkloadTests {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Проверка коэффициента при очень высокой загруженности")
        @DisplayName("Очень высокая загруженность - коэффициент 1.6")
        void shouldApplyVeryHighWorkloadCoefficient() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("50.00"),
                    CargoSize.LARGE,
                    false,
                    DeliveryWorkload.VERY_HIGH
            );
            // (300 + 200) * 1.6 = 800
            assertEquals(new BigDecimal("800.00"), cost);
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Проверка коэффициента при высокой загруженности")
        @DisplayName("Высокая загруженность - коэффициент 1.4")
        void shouldApplyHighWorkloadCoefficient() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("50.00"),
                    CargoSize.LARGE,
                    false,
                    DeliveryWorkload.HIGH
            );
            assertEquals(new BigDecimal("700.00"), cost);
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Проверка коэффициента при повышенной загруженности")
        @DisplayName("Повышенная загруженность - коэффициент 1.2")
        void shouldApplyIncreasedWorkloadCoefficient() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("50.00"),
                    CargoSize.LARGE,
                    false,
                    DeliveryWorkload.INCREASED
            );
            assertEquals(new BigDecimal("600.00"), cost);
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Проверка коэффициента при нормальной загруженности")
        @DisplayName("Нормальная загруженность - коэффициент 1.0")
        void shouldApplyNormalWorkloadCoefficient() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("50.00"),
                    CargoSize.LARGE,
                    false,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("500.00"), cost);
        }
    }

    @Nested
    @Story("Минимальная стоимость доставки")
    @DisplayName("Тесты минимальной стоимости доставки")
    class MinimumCostTests {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Проверка применения минимальной стоимости")
        @DisplayName("Минимальная стоимость доставки - 400 рублей")
        void shouldApplyMinimumDeliveryCost() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("1.00"),
                    CargoSize.SMALL,
                    false,
                    DeliveryWorkload.NORMAL
            );
            assertEquals(new BigDecimal("400.00"), cost);
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @Description("Минимальная стоимость не применяется при превышении")
        @DisplayName("Стоимость выше минимальной не изменяется")
        void shouldNotApplyMinimumWhenCostIsHigher() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("30.00"),
                    CargoSize.LARGE,
                    true,
                    DeliveryWorkload.VERY_HIGH
            );
            assertEquals(new BigDecimal("1120.00"), cost);
        }
    }

    @Nested
    @Story("Валидация входных данных")
    @DisplayName("Валидация входных данных")
    class ValidationTests {

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("Отрицательное расстояние должно вызывать исключение")
        @DisplayName("Исключение при отрицательном расстоянии")
        void shouldThrowExceptionForNegativeDistance() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () ->
                            calculator.calculateDeliveryCost(
                                    new BigDecimal("-1.00"),
                                    CargoSize.SMALL,
                                    false,
                                    DeliveryWorkload.NORMAL
                            )
            );
            assertEquals(
                    "Расстояние должно быть больше 0 км",
                    exception.getMessage()
            );
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("null расстояние должно вызывать исключение")
        @DisplayName("Исключение при null расстоянии")
        void shouldThrowExceptionForNullDistance() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () ->
                            calculator.calculateDeliveryCost(
                                    null,
                                    CargoSize.SMALL,
                                    false,
                                    DeliveryWorkload.NORMAL
                            )
            );
            assertEquals(
                    "Расстояние не может быть null",
                    exception.getMessage()
            );
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("null размер груза должен вызывать исключение")
        @DisplayName("Исключение при null размере груза")
        void shouldThrowExceptionForNullCargoSize() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () ->
                            calculator.calculateDeliveryCost(
                                    new BigDecimal("5.00"),
                                    null,
                                    false,
                                    DeliveryWorkload.NORMAL
                            )
            );
            assertEquals(
                    "Размер груза не может быть null",
                    exception.getMessage()
            );
        }

        @Test
        @Severity(SeverityLevel.CRITICAL)
        @Description("null загрузка должна вызывать исключение")
        @DisplayName("Исключение при null загрузке")
        void shouldThrowExceptionForNullDeliveryWorkload() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () ->
                            calculator.calculateDeliveryCost(
                                    new BigDecimal("5.00"),
                                    CargoSize.SMALL,
                                    false,
                                    null
                            )
            );
            assertEquals(
                    "Загруженность не может быть null",
                    exception.getMessage()
            );
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @Description("Нулевое расстояние должно вызывать исключение")
        @DisplayName("Нулевое расстояние вызывает исключение")
        void shouldThrowIllegalArgumentExceptionForZeroDistance() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () ->
                            calculator.calculateDeliveryCost(
                                    new BigDecimal("0.00"),
                                    CargoSize.SMALL,
                                    false,
                                    DeliveryWorkload.NORMAL
                            )
            );
            assertEquals(
                    "Расстояние должно быть больше 0 км",
                    exception.getMessage()
            );
        }
    }

    @Nested
    @Story("Комплексные сценарии")
    @DisplayName("Комплексные тестовые сценарии")
    class ComplexScenariosTests {

        @ParameterizedTest
        @MethodSource("complexScenarioProvider")
        @Severity(SeverityLevel.NORMAL)
        @Description(
                "Комплексные сценарии с различными комбинациями параметров"
        )
        @DisplayName("Проверка комплексных сценариев")
        void shouldCalculateComplexScenarios(
                BigDecimal distance,
                CargoSize cargoSize,
                boolean isFragile,
                DeliveryWorkload workloadLevel,
                BigDecimal expectedCost
        ) {
            BigDecimal actualCost = calculator.calculateDeliveryCost(
                    distance,
                    cargoSize,
                    isFragile,
                    workloadLevel
            );
            assertEquals(expectedCost, actualCost);
        }

        static Stream<Arguments> complexScenarioProvider() {
            return Stream.of(
                    Arguments.of(
                            new BigDecimal("1.00"),
                            CargoSize.SMALL,
                            false,
                            DeliveryWorkload.NORMAL,
                            new BigDecimal("400.00")
                    ),
                    Arguments.of(
                            new BigDecimal("5.00"),
                            CargoSize.SMALL,
                            false,
                            DeliveryWorkload.NORMAL,
                            new BigDecimal("400.00")
                    ),
                    Arguments.of(
                            new BigDecimal("15.00"),
                            CargoSize.LARGE,
                            false,
                            DeliveryWorkload.HIGH,
                            new BigDecimal("560.00")
                    ),
                    Arguments.of(
                            new BigDecimal("25.00"),
                            CargoSize.SMALL,
                            true,
                            DeliveryWorkload.INCREASED,
                            new BigDecimal("720.00")
                    ),
                    Arguments.of(
                            new BigDecimal("50.00"),
                            CargoSize.LARGE,
                            false,
                            DeliveryWorkload.VERY_HIGH,
                            new BigDecimal("800.00")
                    ),
                    Arguments.of(
                            new BigDecimal("30.00"),
                            CargoSize.LARGE,
                            true,
                            DeliveryWorkload.VERY_HIGH,
                            new BigDecimal("1120.00")
                    ),
                    Arguments.of(
                            new BigDecimal("2.00"),
                            CargoSize.LARGE,
                            false,
                            DeliveryWorkload.VERY_HIGH,
                            new BigDecimal("400.00")
                    ),
                    Arguments.of(
                            new BigDecimal("10.00"),
                            CargoSize.SMALL,
                            false,
                            DeliveryWorkload.VERY_HIGH,
                            new BigDecimal("400.00")
                    ),
                    Arguments.of(
                            new BigDecimal("30.00"),
                            CargoSize.SMALL,
                            false,
                            DeliveryWorkload.VERY_HIGH,
                            new BigDecimal("480.00")
                    )
            );
        }

        @Test
        @Severity(SeverityLevel.NORMAL)
        @Description("Максимально возможная стоимость")
        @DisplayName(
                "Максимальная стоимость: дальнее расстояние + большой хрупкий груз + максимальная загруженность"
        )
        void shouldCalculateMaximumPossibleCost() {
            BigDecimal cost = calculator.calculateDeliveryCost(
                    new BigDecimal("30.00"),
                    CargoSize.LARGE,
                    true,
                    DeliveryWorkload.VERY_HIGH
            );
            assertEquals(new BigDecimal("1120.00"), cost);
        }
    }
}
