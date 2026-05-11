package co.edu.carbon.model;

import co.edu.carbon.model.Bicycle.BikeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BicycleTest — Pruebas unitarias para la clase Bicycle
 *
 * Cubre:
 *  - Construcción válida e inválida
 *  - Cálculo correcto de huella (fabricación + alimentaria)
 *  - Comportamiento con todos los tipos de bicicleta
 *  - Relación huella vs kilómetros
 *  - Getters y métodos de presentación
 *
 */
@DisplayName("Bicycle — Pruebas Unitarias")
class BicycleTest {

    private static final double MANUFACTURING_KG_CO2 = 96.0;
    private static final double KCAL_PER_KM           = 25.0;
    private static final double KG_CO2_PER_1000_KCAL  = 0.16;
    private static final double DELTA                  = 0.0001;

    private Bicycle bicycle;

    @BeforeEach
    void setUp() {
        // Trek Road, 8.5 kg, 4000 km/año, 15 años de vida útil
        bicycle = new Bicycle("Trek", BikeType.ROAD, 8.5, 4_000.0, 15);
    }

    // ── Construcción válida ───────────────────────────────────────────────

    @Nested
    @DisplayName("Constructor — casos válidos")
    class ValidConstruction {

        @Test
        @DisplayName("Debería crear el objeto con los atributos correctos")
        void shouldCreateWithCorrectAttributes() {
            assertEquals("Trek",          bicycle.getBrand());
            assertEquals(BikeType.ROAD,   bicycle.getType());
            assertEquals(8.5,             bicycle.getWeightKg(),            DELTA);
            assertEquals(4_000.0,         bicycle.getAnnualKm(),             DELTA);
            assertEquals(15,              bicycle.getLifeExpectancyYears());
        }

        @Test
        @DisplayName("Debería permitir 0 km anuales (bicicleta guardada)")
        void shouldAllowZeroAnnualKm() {
            Bicycle stored = new Bicycle("Generic", BikeType.URBAN, 10.0, 0, 10);
            // huella = solo fabricación
            double expected = MANUFACTURING_KG_CO2 / 10.0;
            assertEquals(expected, stored.getCarbonFootprint(), DELTA);
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
                () -> new Bicycle("", BikeType.MTB, 10.0, 2_000, 10));
        }

        @Test
        @DisplayName("Debería lanzar excepción con tipo null")
        void shouldThrowOnNullType() {
            assertThrows(IllegalArgumentException.class,
                () -> new Bicycle("Trek", null, 10.0, 2_000, 10));
        }

        @Test
        @DisplayName("Debería lanzar excepción con peso <= 0")
        void shouldThrowOnZeroWeight() {
            assertThrows(IllegalArgumentException.class,
                () -> new Bicycle("Trek", BikeType.MTB, 0, 2_000, 10));
        }

        @Test
        @DisplayName("Debería lanzar excepción con kilómetros negativos")
        void shouldThrowOnNegativeKm() {
            assertThrows(IllegalArgumentException.class,
                () -> new Bicycle("Trek", BikeType.MTB, 10.0, -100, 10));
        }

        @Test
        @DisplayName("Debería lanzar excepción con vida útil <= 0")
        void shouldThrowOnZeroLifeExpectancy() {
            assertThrows(IllegalArgumentException.class,
                () -> new Bicycle("Trek", BikeType.MTB, 10.0, 2_000, 0));
        }
    }

    // ── Cálculo de huella ─────────────────────────────────────────────────

    @Nested
    @DisplayName("getCarbonFootprint() — cálculo")
    class FootprintCalculation {

        @Test
        @DisplayName("Debería calcular correctamente con valores conocidos")
        void shouldCalculateCorrectly() {
            double manufacturingPerYear = MANUFACTURING_KG_CO2 / 15.0;
            double foodFootprint        = (4_000.0 * KCAL_PER_KM / 1000.0)
                                          * KG_CO2_PER_1000_KCAL;
            double expected = manufacturingPerYear + foodFootprint;
            assertEquals(expected, bicycle.getCarbonFootprint(), DELTA);
        }

        @Test
        @DisplayName("La huella debe ser mayor que cero incluso sin km")
        void footprintPositiveEvenWithZeroKm() {
            Bicycle noRide = new Bicycle("Trek", BikeType.ROAD, 8.5, 0, 15);
            assertTrue(noRide.getCarbonFootprint() > 0,
                       "La huella de fabricación siempre debe ser positiva");
        }

        @Test
        @DisplayName("Mayor vida útil → menor huella por fabricación")
        void longerLifeMeansLowerManufacturingFootprint() {
            Bicycle longLife  = new Bicycle("A", BikeType.URBAN, 10.0, 2_000, 20);
            Bicycle shortLife = new Bicycle("B", BikeType.URBAN, 10.0, 2_000, 5);
            assertTrue(longLife.getCarbonFootprint() < shortLife.getCarbonFootprint());
        }

        @Test
        @DisplayName("Mayor km/año → mayor huella alimentaria → mayor huella total")
        void moreKmMeansHigherFootprint() {
            Bicycle highMileage = new Bicycle("Trek", BikeType.ROAD, 8.5, 8_000, 15);
            assertTrue(highMileage.getCarbonFootprint() > bicycle.getCarbonFootprint());
        }

        @ParameterizedTest(name = "BikeType {0} → huella siempre > 0")
        @EnumSource(BikeType.class)
        @DisplayName("Todos los tipos de bicicleta producen huella positiva")
        void allBikeTypesShouldProducePositiveFootprint(BikeType type) {
            Bicycle b = new Bicycle("Test", type, 10.0, 3_000, 10);
            assertTrue(b.getCarbonFootprint() > 0);
        }
    }

    // ── Métodos de presentación ───────────────────────────────────────────

    @Nested
    @DisplayName("Métodos de presentación")
    class PresentationMethods {

        @Test
        @DisplayName("getIdentifierInfo() debe contener la marca")
        void identifierInfoShouldContainBrand() {
            assertTrue(bicycle.getIdentifierInfo().contains("Trek"));
        }

        @Test
        @DisplayName("getIdentifierInfo() debe contener la etiqueta [Bicicleta]")
        void identifierInfoShouldContainTag() {
            assertTrue(bicycle.getIdentifierInfo().contains("[Bicicleta]"));
        }

        @Test
        @DisplayName("toString() debe incluir los atributos principales")
        void toStringShouldIncludeMainAttributes() {
            String str = bicycle.toString();
            assertTrue(str.contains("Trek"));
            assertTrue(str.contains("ROAD"));
            assertTrue(str.contains("15"));
        }
    }
}
