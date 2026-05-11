package co.edu.carbon.app;

import co.edu.carbon.interfaces.CarbonFootprint;
import co.edu.carbon.model.Bicycle;
import co.edu.carbon.model.Building;
import co.edu.carbon.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MainTest — Pruebas de integración para la clase Main
 *
 * Verifica que el método buildEntities() construya correctamente la lista
 * de objetos y que el loop polimórfico opere sobre ella sin errores.
 *
 */
@DisplayName("Main — Pruebas de Integración")
class MainTest {

    @Nested
    @DisplayName("buildEntities() — construcción de la lista")
    class BuildEntitiesTests {

        @Test
        @DisplayName("Debe devolver una lista no nula")
        void listShouldNotBeNull() {
            assertNotNull(Main.buildEntities());
        }

        @Test
        @DisplayName("La lista debe contener al menos un objeto de cada tipo")
        void listShouldContainAllThreeTypes() {
            List<CarbonFootprint> list = Main.buildEntities();

            boolean hasBuilding = list.stream().anyMatch(o -> o instanceof Building);
            boolean hasCar      = list.stream().anyMatch(o -> o instanceof Car);
            boolean hasBicycle  = list.stream().anyMatch(o -> o instanceof Bicycle);

            assertTrue(hasBuilding, "La lista debe tener al menos un Building");
            assertTrue(hasCar,      "La lista debe tener al menos un Car");
            assertTrue(hasBicycle,  "La lista debe tener al menos un Bicycle");
        }

        @Test
        @DisplayName("Ningún objeto de la lista debe ser null")
        void noNullObjectsInList() {
            List<CarbonFootprint> list = Main.buildEntities();
            list.forEach(o -> assertNotNull(o, "Se encontró un null en la lista"));
        }
    }

    @Nested
    @DisplayName("printReport() — iteración polimórfica")
    class PrintReportTests {

        @Test
        @DisplayName("No debe lanzar excepción durante la impresión")
        void printShouldNotThrow() {
            assertDoesNotThrow(() ->
                Main.printReport(Main.buildEntities()));
        }

        @Test
        @DisplayName("La suma total de huellas debe ser positiva")
        void totalFootprintPositive() {
            double total = Main.buildEntities()
                               .stream()
                               .mapToDouble(CarbonFootprint::getCarbonFootprint)
                               .sum();
            assertTrue(total > 0);
        }
    }
}
