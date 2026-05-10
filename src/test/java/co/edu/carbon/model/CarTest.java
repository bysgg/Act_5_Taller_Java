package co.edu.carbon.model;

import co.edu.carbon.model.Car.FuelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CarTest — Pruebas unitarias para la clase Car
 *
 * Cubre:
 *  - Construcción válida e inválida
 *  - Cálculo correcto para los tres tipos de combustible
 *  - Comportamiento paramétrico con @EnumSource
 *  - Casos borde (0 km/año)
 *  - Getters y métodos de presentación
 *
 * @author Carbon Footprint App
 * @version 1.0
 */
@DisplayName("Car — Pruebas Unitarias")
class CarTest {

    private static final double DELTA = 0.001;

    private Car car;

    @BeforeEach
    void setUp() {
        // Toyota Corolla gasolina, 13.5 km/L, 20 000 km/año
        car = new Car("Toyota", "Corolla", 2021,
                      FuelType.GASOLINE, 13.5, 20_000.0);
    }

    // ── Construcción válida ───────────────────────────────────────────────

    @Nested
    @DisplayName("Constructor — casos válidos")
    class ValidConstruction {

        @Test
        @DisplayName("Debería crear el objeto con los atributos correctos")
        void shouldCreateWithCorrectAttributes() {
            assertEquals("Toyota",       car.getBrand());
            assertEquals("Corolla",      car.getModel());
            assertEquals(2021,           car.getYear());
            assertEquals(FuelType.GASOLINE, car.getFuelType());
            assertEquals(13.5,           car.getEfficiencyKmPerLiter(), DELTA);
            assertEquals(20_000.0,       car.getAnnualKm(), DELTA);
        }

        @Test
        @DisplayName("Debería permitir 0 kilómetros anuales")
        void shouldAllowZeroAnnualKm() {
            Car parkedCar = new Car("Honda", "Civic", 2020,
                                   FuelType.GASOLINE, 12.0, 0);
            assertEquals(0.0, parkedCar.getCarbonFootprint(), DELTA);
        }
    }

    // ── Construcción inválida ─────────────────────────────────────────────

    @Nested
    @DisplayName("Constructor — casos inválidos")
    class InvalidConstruction {

        @Test
        @DisplayName("Debería lanzar excepción con marca vacía")
        void shouldThrowOnEmptyBrand() {
            assertThrows(IllegalArgumentException.class,
                () -> new Car("", "Civic", 2020, FuelType.GASOLINE, 12.0, 10_000));
        }

        @Test
        @DisplayName("Debería lanzar excepción con modelo null")
        void shouldThrowOnNullModel() {
            assertThrows(IllegalArgumentException.class,
                () -> new Car("Honda", null, 2020, FuelType.GASOLINE, 12.0, 10_000));
        }

        @Test
        @DisplayName("Debería lanzar excepción con año anterior a 1886")
        void shouldThrowOnInvalidYear() {
            assertThrows(IllegalArgumentException.class,
                () -> new Car("Ford", "T", 1800, FuelType.GASOLINE, 5.0, 5_000));
        }

        @Test
        @DisplayName("Debería lanzar excepción con eficiencia <= 0")
        void shouldThrowOnZeroEfficiency() {
            assertThrows(IllegalArgumentException.class,
                () -> new Car("Kia", "Rio", 2021, FuelType.GASOLINE, 0, 10_000));
        }

        @Test
        @DisplayName("Debería lanzar excepción con km anuales negativos")
        void shouldThrowOnNegativeKm() {
            assertThrows(IllegalArgumentException.class,
                () -> new Car("Kia", "Rio", 2021, FuelType.GASOLINE, 12.0, -1));
        }

        @Test
        @DisplayName("Debería lanzar excepción con fuelType null")
        void shouldThrowOnNullFuelType() {
            assertThrows(IllegalArgumentException.class,
                () -> new Car("Kia", "Rio", 2021, null, 12.0, 10_000));
        }
    }

    // ── Cálculo de huella ─────────────────────────────────────────────────

    @Nested
    @DisplayName("getCarbonFootprint() — cálculo")
    class FootprintCalculation {

        @Test
        @DisplayName("Debería calcular correctamente para gasolina")
        void shouldCalculateGasolineFootprint() {
            // litros = 20 000 / 13.5 ≈ 1481.48
            // huella = 1481.48 × 2.31 ≈ 3 422.22
            double expected = (20_000.0 / 13.5) * FuelType.GASOLINE.getEmissionFactor();
            assertEquals(expected, car.getCarbonFootprint(), DELTA);
        }

        @Test
        @DisplayName("Debería calcular correctamente para diésel")
        void shouldCalculateDieselFootprint() {
            Car diesel = new Car("Mazda", "CX-5", 2019,
                                 FuelType.DIESEL, 16.0, 30_000.0);
            double expected = (30_000.0 / 16.0) * FuelType.DIESEL.getEmissionFactor();
            assertEquals(expected, diesel.getCarbonFootprint(), DELTA);
        }

        @Test
        @DisplayName("Debería calcular correctamente para GNV")
        void shouldCalculateCngFootprint() {
            Car gnv = new Car("Chevrolet", "Spark", 2022,
                              FuelType.CNG, 11.2, 15_000.0);
            double expected = (15_000.0 / 11.2) * FuelType.CNG.getEmissionFactor();
            assertEquals(expected, gnv.getCarbonFootprint(), DELTA);
        }

        @ParameterizedTest(name = "Combustible {0} → huella > 0 con km > 0")
        @EnumSource(FuelType.class)
        @DisplayName("Todos los tipos de combustible producen huella positiva")
        void allFuelTypesShouldProducePositiveFootprint(FuelType fuelType) {
            Car testCar = new Car("Test", "Model", 2022, fuelType, 12.0, 10_000);
            assertTrue(testCar.getCarbonFootprint() > 0);
        }

        @Test
        @DisplayName("Diésel debe tener mayor factor que gasolina → mayor huella proporcional")
        void dieselShouldEmitMoreThanGasolinePerLiter() {
            assertTrue(FuelType.DIESEL.getEmissionFactor() >
                       FuelType.GASOLINE.getEmissionFactor());
        }
    }

    // ── Métodos de presentación ───────────────────────────────────────────

    @Nested
    @DisplayName("Métodos de presentación")
    class PresentationMethods {

        @Test
        @DisplayName("getIdentifierInfo() debe contener marca y modelo")
        void identifierInfoShouldContainBrandAndModel() {
            String info = car.getIdentifierInfo();
            assertTrue(info.contains("Toyota"));
            assertTrue(info.contains("Corolla"));
        }

        @Test
        @DisplayName("getIdentifierInfo() debe contener la etiqueta [Auto]")
        void identifierInfoShouldContainTag() {
            assertTrue(car.getIdentifierInfo().contains("[Auto]"));
        }

        @Test
        @DisplayName("toString() debe incluir los atributos principales")
        void toStringShouldIncludeMainAttributes() {
            String str = car.toString();
            assertTrue(str.contains("Toyota"));
            assertTrue(str.contains("Corolla"));
            assertTrue(str.contains("2021"));
            assertTrue(str.contains("GASOLINE"));
        }
    }
}
