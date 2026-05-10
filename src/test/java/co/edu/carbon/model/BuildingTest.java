package co.edu.carbon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BuildingTest — Pruebas unitarias para la clase Building
 *
 * Cubre:
 *  - Construcción válida e inválida
 *  - Cálculo correcto de huella de carbono
 *  - Getters y métodos de presentación
 *  - Validación de argumentos (defensiva)
 *
 * @author Carbon Footprint App
 * @version 1.0
 */
@DisplayName("Building — Pruebas Unitarias")
class BuildingTest {

    // Factores (deben coincidir con los de Building)
    private static final double GAS_FACTOR   = 2.204;
    private static final double ELEC_FACTOR  = 0.233;
    private static final double DELTA        = 0.001;

    private Building building;

    @BeforeEach
    void setUp() {
        // Edificio de referencia: 12 pisos, 5000 m², 1000 m³ gas/mes, 50 000 kWh/mes
        building = new Building("Edificio Central", 12, 5000.0, 1000.0, 50_000.0);
    }

    // ── Construcción ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("Constructor — casos válidos")
    class ValidConstruction {

        @Test
        @DisplayName("Debería crear el objeto con los atributos correctos")
        void shouldCreateWithCorrectAttributes() {
            assertEquals("Edificio Central", building.getName());
            assertEquals(12,      building.getFloors());
            assertEquals(5000.0,  building.getBuiltAreaM2(),  DELTA);
            assertEquals(1000.0,  building.getMonthlyGasM3(), DELTA);
            assertEquals(50_000.0, building.getMonthlyElectricityKwh(), DELTA);
        }

        @Test
        @DisplayName("Debería permitir consumo de gas = 0 (edificio eléctrico)")
        void shouldAllowZeroGasConsumption() {
            Building elecBuilding = new Building("Torre Solar", 5, 2000.0, 0, 30_000.0);
            assertTrue(elecBuilding.getCarbonFootprint() > 0);
        }

        @Test
        @DisplayName("Debería permitir consumo de electricidad = 0")
        void shouldAllowZeroElectricityConsumption() {
            Building gasBuilding = new Building("Casa Gas", 1, 200.0, 100.0, 0);
            assertTrue(gasBuilding.getCarbonFootprint() > 0);
        }
    }

    @Nested
    @DisplayName("Constructor — casos inválidos")
    class InvalidConstruction {

        @Test
        @DisplayName("Debería lanzar excepción con nombre vacío")
        void shouldThrowOnEmptyName() {
            assertThrows(IllegalArgumentException.class,
                () -> new Building("", 5, 1000.0, 100.0, 5000.0));
        }

        @Test
        @DisplayName("Debería lanzar excepción con nombre null")
        void shouldThrowOnNullName() {
            assertThrows(IllegalArgumentException.class,
                () -> new Building(null, 5, 1000.0, 100.0, 5000.0));
        }

        @Test
        @DisplayName("Debería lanzar excepción con pisos <= 0")
        void shouldThrowOnNonPositiveFloors() {
            assertThrows(IllegalArgumentException.class,
                () -> new Building("Torre", 0, 1000.0, 100.0, 5000.0));
        }

        @Test
        @DisplayName("Debería lanzar excepción con área <= 0")
        void shouldThrowOnNonPositiveArea() {
            assertThrows(IllegalArgumentException.class,
                () -> new Building("Torre", 5, 0, 100.0, 5000.0));
        }

        @Test
        @DisplayName("Debería lanzar excepción con gas negativo")
        void shouldThrowOnNegativeGas() {
            assertThrows(IllegalArgumentException.class,
                () -> new Building("Torre", 5, 1000.0, -1.0, 5000.0));
        }

        @Test
        @DisplayName("Debería lanzar excepción con electricidad negativa")
        void shouldThrowOnNegativeElectricity() {
            assertThrows(IllegalArgumentException.class,
                () -> new Building("Torre", 5, 1000.0, 100.0, -1.0));
        }
    }

    // ── Cálculo de huella ─────────────────────────────────────────────────

    @Nested
    @DisplayName("getCarbonFootprint() — cálculo")
    class FootprintCalculation {

        @Test
        @DisplayName("Debería calcular la huella correctamente con valores conocidos")
        void shouldCalculateCorrectly() {
            // gas anual = 1000 × 12 = 12 000 m³
            // elec anual = 50 000 × 12 = 600 000 kWh
            double expected = (12_000 * GAS_FACTOR) + (600_000 * ELEC_FACTOR);
            assertEquals(expected, building.getCarbonFootprint(), DELTA);
        }

        @Test
        @DisplayName("La huella debe ser mayor o igual a cero")
        void footprintShouldBeNonNegative() {
            assertTrue(building.getCarbonFootprint() >= 0);
        }

        @Test
        @DisplayName("Mayor consumo → mayor huella")
        void higherConsumptionMeansHigherFootprint() {
            Building bigger = new Building("Mega Torre", 30, 20_000.0, 5000.0, 200_000.0);
            assertTrue(bigger.getCarbonFootprint() > building.getCarbonFootprint());
        }

        @Test
        @DisplayName("Cero consumos → huella = 0")
        void zeroBothConsumptionsGivesZero() {
            Building zeroBuilding = new Building("Edificio Verde", 1, 100.0, 0, 0);
            assertEquals(0.0, zeroBuilding.getCarbonFootprint(), DELTA);
        }
    }

    // ── Métodos de presentación ───────────────────────────────────────────

    @Nested
    @DisplayName("Métodos de presentación")
    class PresentationMethods {

        @Test
        @DisplayName("getIdentifierInfo() debe contener el nombre del edificio")
        void identifierInfoShouldContainName() {
            assertTrue(building.getIdentifierInfo().contains("Edificio Central"));
        }

        @Test
        @DisplayName("getIdentifierInfo() debe contener la etiqueta [Edificio]")
        void identifierInfoShouldContainTag() {
            assertTrue(building.getIdentifierInfo().contains("[Edificio]"));
        }

        @Test
        @DisplayName("toString() debe incluir los atributos principales")
        void toStringShouldIncludeMainAttributes() {
            String str = building.toString();
            assertTrue(str.contains("Edificio Central"));
            assertTrue(str.contains("12"));
        }
    }
}
