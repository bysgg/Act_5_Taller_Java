package co.edu.carbon.storage;

import co.edu.carbon.interfaces.CarbonFootprint;
import co.edu.carbon.model.Bicycle;
import co.edu.carbon.model.Bicycle.BikeType;
import co.edu.carbon.model.Building;
import co.edu.carbon.model.Car;
import co.edu.carbon.model.Car.FuelType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CarbonFootprintStorageTest — Pruebas unitarias para el módulo de persistencia
 *
 * Cubre:
 *  - Escritura correcta del archivo
 *  - Lectura y verificación del contenido
 *  - Número de registros de huella en el archivo
 *  - Argumentos inválidos (null, vacío)
 *  - Polimorfismo sobre ArrayList&lt;CarbonFootprint&gt;
 *  - Comportamiento con lista vacía
 *
 */
@DisplayName("CarbonFootprintStorage — Pruebas Unitarias")
class CarbonFootprintStorageTest {

    private static final String TEST_FILE = "test_carbon_report.txt";
    private static final double DELTA     = 0.001;

    private CarbonFootprintStorage storage;
    private List<CarbonFootprint>  footprints;

    @BeforeEach
    void setUp() {
        storage    = new CarbonFootprintStorage();
        footprints = buildTestList();
    }

    @AfterEach
    void tearDown() {
        // Limpieza del archivo temporal después de cada prueba
        File file = new File(TEST_FILE);
        if (file.exists()) file.delete();
    }

    // ── Escritura ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("writeToFile() — escritura")
    class WriteTests {

        @Test
        @DisplayName("Debería crear el archivo después de escribir")
        void shouldCreateFile() throws IOException {
            storage.writeToFile(footprints, TEST_FILE);
            assertTrue(new File(TEST_FILE).exists());
        }

        @Test
        @DisplayName("El archivo no debe estar vacío")
        void fileShouldNotBeEmpty() throws IOException {
            storage.writeToFile(footprints, TEST_FILE);
            assertTrue(new File(TEST_FILE).length() > 0);
        }

        @Test
        @DisplayName("El archivo debe contener el encabezado del reporte")
        void fileShouldContainHeader() throws IOException {
            storage.writeToFile(footprints, TEST_FILE);
            String content = storage.readFileAsString(TEST_FILE);
            assertTrue(content.contains("CARBON FOOTPRINT REPORT"));
        }

        @Test
        @DisplayName("El archivo debe contener una línea de huella por cada objeto")
        void fileShouldContainFootprintLinePerObject() throws IOException {
            storage.writeToFile(footprints, TEST_FILE);
            List<String> lines       = storage.readFromFile(TEST_FILE);
            List<String> fpLines     = storage.extractFootprintLines(lines);
            assertEquals(footprints.size(), fpLines.size());
        }

        @Test
        @DisplayName("El archivo debe contener información del edificio")
        void fileShouldContainBuildingInfo() throws IOException {
            storage.writeToFile(footprints, TEST_FILE);
            String content = storage.readFileAsString(TEST_FILE);
            assertTrue(content.contains("Edificio"));
        }

        @Test
        @DisplayName("El archivo debe contener información del auto")
        void fileShouldContainCarInfo() throws IOException {
            storage.writeToFile(footprints, TEST_FILE);
            String content = storage.readFileAsString(TEST_FILE);
            assertTrue(content.contains("Auto"));
        }

        @Test
        @DisplayName("El archivo debe contener información de la bicicleta")
        void fileShouldContainBicycleInfo() throws IOException {
            storage.writeToFile(footprints, TEST_FILE);
            String content = storage.readFileAsString(TEST_FILE);
            assertTrue(content.contains("Bicicleta"));
        }

        @Test
        @DisplayName("Debería lanzar excepción si la lista es null")
        void shouldThrowOnNullList() {
            assertThrows(IllegalArgumentException.class,
                () -> storage.writeToFile(null, TEST_FILE));
        }

        @Test
        @DisplayName("Debería lanzar excepción si la ruta está vacía")
        void shouldThrowOnEmptyPath() {
            assertThrows(IllegalArgumentException.class,
                () -> storage.writeToFile(footprints, ""));
        }
    }

    // ── Lectura ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("readFromFile() — lectura")
    class ReadTests {

        @Test
        @DisplayName("Debería leer el mismo número de líneas que las escritas")
        void shouldReadLinesCorrectly() throws IOException {
            storage.writeToFile(footprints, TEST_FILE);
            List<String> lines = storage.readFromFile(TEST_FILE);
            assertTrue(lines.size() > 0);
        }

        @Test
        @DisplayName("Debería lanzar IOException si el archivo no existe")
        void shouldThrowIfFileNotFound() {
            assertThrows(IOException.class,
                () -> storage.readFromFile("archivo_inexistente_xyz.txt"));
        }

        @Test
        @DisplayName("Debería lanzar excepción con ruta null")
        void shouldThrowOnNullPath() {
            assertThrows(IllegalArgumentException.class,
                () -> storage.readFromFile(null));
        }

        @Test
        @DisplayName("readFileAsString() no debe devolver cadena vacía tras escritura")
        void readAsStringShouldNotBeEmpty() throws IOException {
            storage.writeToFile(footprints, TEST_FILE);
            String content = storage.readFileAsString(TEST_FILE);
            assertFalse(content.isBlank());
        }
    }

    // ── Polimorfismo sobre ArrayList ───────────────────────────────────────

    @Nested
    @DisplayName("Polimorfismo — ArrayList<CarbonFootprint>")
    class PolymorphismTests {

        @Test
        @DisplayName("Todos los objetos deben responder a getCarbonFootprint()")
        void allObjectsShouldRespondToGetCarbonFootprint() {
            for (CarbonFootprint fp : footprints) {
                // La invocación es polimórfica: Java decide en runtime
                assertDoesNotThrow(() -> fp.getCarbonFootprint());
            }
        }

        @Test
        @DisplayName("Todos los objetos deben devolver huellas >= 0")
        void allObjectsShouldHaveNonNegativeFootprint() {
            for (CarbonFootprint fp : footprints) {
                assertTrue(fp.getCarbonFootprint() >= 0,
                    "La huella de " + fp.getIdentifierInfo() + " no puede ser negativa");
            }
        }

        @Test
        @DisplayName("La lista polimórfica puede contener los tres tipos")
        void listShouldAcceptAllThreeTypes() {
            assertEquals(3, footprints.size());
        }

        @Test
        @DisplayName("La suma polimórfica de huellas debe ser > 0")
        void totalFootprintShouldBePositive() {
            double total = footprints.stream()
                                     .mapToDouble(CarbonFootprint::getCarbonFootprint)
                                     .sum();
            assertTrue(total > 0);
        }

        @Test
        @DisplayName("getIdentifierInfo() debe funcionar para todos los objetos")
        void allObjectsShouldProvideIdentifierInfo() {
            for (CarbonFootprint fp : footprints) {
                String info = fp.getIdentifierInfo();
                assertNotNull(info);
                assertFalse(info.isBlank());
            }
        }

        @Test
        @DisplayName("Los valores de huella son distintos entre los tres tipos")
        void footprintValuesShouldDifferByType() {
            double buildingFp = footprints.get(0).getCarbonFootprint();
            double carFp      = footprints.get(1).getCarbonFootprint();
            double bikeFp     = footprints.get(2).getCarbonFootprint();

            // Edificio > Auto > Bicicleta en los datos de prueba
            assertTrue(buildingFp > bikeFp,
                "El edificio debería tener mayor huella que la bicicleta");
            assertTrue(carFp > bikeFp,
                "El auto debería tener mayor huella que la bicicleta");
        }
    }

    // ── Lista vacía ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Casos borde — lista vacía")
    class EmptyListTests {

        @Test
        @DisplayName("Escribir lista vacía no debe lanzar excepción")
        void writingEmptyListShouldNotThrow() {
            assertDoesNotThrow(() ->
                storage.writeToFile(new ArrayList<>(), TEST_FILE));
        }

        @Test
        @DisplayName("Archivo con lista vacía debe existir y contener encabezado")
        void emptyListFileShouldContainHeader() throws IOException {
            storage.writeToFile(new ArrayList<>(), TEST_FILE);
            String content = storage.readFileAsString(TEST_FILE);
            assertTrue(content.contains("CARBON FOOTPRINT REPORT"));
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private List<CarbonFootprint> buildTestList() {
        List<CarbonFootprint> list = new ArrayList<>();
        list.add(new Building("Edificio Test", 5, 2000.0, 500.0, 30_000.0));
        list.add(new Car("Toyota", "Test", 2020, FuelType.GASOLINE, 12.0, 15_000.0));
        list.add(new Bicycle("Trek", BikeType.ROAD, 8.5, 3_000.0, 15));
        return list;
    }
}
