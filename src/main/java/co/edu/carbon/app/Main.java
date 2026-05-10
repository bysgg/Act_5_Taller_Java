package co.edu.carbon.app;

import co.edu.carbon.interfaces.CarbonFootprint;
import co.edu.carbon.model.Bicycle;
import co.edu.carbon.model.Bicycle.BikeType;
import co.edu.carbon.model.Building;
import co.edu.carbon.model.Car;
import co.edu.carbon.model.Car.FuelType;
import co.edu.carbon.storage.CarbonFootprintStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main — Clase principal de la aplicación Carbon Footprint
 *
 * Demuestra el uso de:
 *  - Interfaz CarbonFootprint (polimorfismo / abstracción)
 *  - Clases Building, Car y Bicycle (modularidad / encapsulamiento)
 *  - ArrayList&lt;CarbonFootprint&gt; con iteración polimórfica
 *  - Persistencia en archivo de texto (.txt)
 *
 * Flujo:
 *   1. Instanciar objetos de las tres clases
 *   2. Almacenarlos en ArrayList&lt;CarbonFootprint&gt;
 *   3. Iterar e invocar getCarbonFootprint() polimórficamente
 *   4. Imprimir resultados en consola
 *   5. Persistir el reporte en archivo de texto
 *   6. Verificar la lectura del archivo
 *
 * @author Carbon Footprint App
 * @version 1.0
 */
public class Main {

    /** Ruta del archivo de salida */
    private static final String OUTPUT_FILE = "carbon_footprint_report.txt";

    /** Separador visual para consola */
    private static final String LINE =
        "═══════════════════════════════════════════════════════════════════════";

    public static void main(String[] args) {

        // ── 1. Crear instancias de las tres clases ─────────────────────────
        List<CarbonFootprint> entities = buildEntities();

        // ── 2. Imprimir reporte polimórfico en consola ─────────────────────
        printReport(entities);

        // ── 3. Persistir en archivo de texto ──────────────────────────────
        persistReport(entities);

        // ── 4. Leer y confirmar el archivo ────────────────────────────────
        verifyFile();
    }

    // ──────────────────────────────────────────────────────────────────────
    // Helpers públicos (reutilizables en pruebas)
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Construye la lista de objetos CarbonFootprint con datos realistas.
     * Método separado para facilitar pruebas unitarias.
     *
     * @return ArrayList con objetos Building, Car y Bicycle
     */
    public static List<CarbonFootprint> buildEntities() {
        List<CarbonFootprint> list = new ArrayList<>();

        // Edificios
        list.add(new Building(
            "Torre Colpatria",   /*pisos*/ 48, /*m²*/ 28_000,
            /*gasM3/mes*/ 4_500, /*kWh/mes*/ 380_000));

        list.add(new Building(
            "Residencias El Prado", 6, 3_200,
            /*gasM3/mes*/ 420, /*kWh/mes*/ 22_000));

        // Automóviles
        list.add(new Car(
            "Toyota", "Corolla",  2021,
            FuelType.GASOLINE, /*km/L*/ 13.5, /*kmAño*/ 20_000));

        list.add(new Car(
            "Mazda", "CX-5",   2019,
            FuelType.DIESEL,   /*km/L*/ 16.0, /*kmAño*/ 30_000));

        list.add(new Car(
            "Chevrolet", "Spark GT", 2022,
            FuelType.CNG,      /*km/L*/ 11.2, /*kmAño*/ 15_000));

        // Bicicletas
        list.add(new Bicycle(
            "Trek",     BikeType.ROAD,  /*kg*/ 8.5,
            /*kmAño*/  4_000, /*vidaÚtil*/ 15));

        list.add(new Bicycle(
            "Specialized", BikeType.MTB, /*kg*/ 12.0,
            /*kmAño*/ 2_500, /*vidaÚtil*/ 12));

        list.add(new Bicycle(
            "Brompton", BikeType.FOLDING, /*kg*/ 11.5,
            /*kmAño*/ 1_800, /*vidaÚtil*/ 20));

        return list;
    }

    /**
     * Imprime en consola el reporte completo, iterando de forma polimórfica.
     *
     * @param entities lista de objetos CarbonFootprint
     */
    public static void printReport(List<CarbonFootprint> entities) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("   CARBON FOOTPRINT — REPORTE DE HUELLA DE CARBONO ANUAL");
        System.out.println(LINE);
        System.out.println();

        double totalFootprint = 0;
        int    index          = 1;

        // ── LOOP POLIMÓRFICO ──────────────────────────────────────────────
        // Se invoca getCarbonFootprint() sobre CarbonFootprint sin conocer
        // la clase concreta; Java resuelve en tiempo de ejecución (late binding)
        for (CarbonFootprint entity : entities) {
            double footprint = entity.getCarbonFootprint();
            totalFootprint  += footprint;

            System.out.printf("  #%-2d %s%n", index, entity.getIdentifierInfo());
            System.out.printf("       ↳ Huella de carbono: %,.2f kgCO₂/año%n%n",
                              footprint);
            index++;
        }

        // ── Resumen ───────────────────────────────────────────────────────
        System.out.println(LINE);
        System.out.printf("  Total de objetos  : %d%n", entities.size());
        System.out.printf("  Huella acumulada  : %,.2f kgCO₂/año%n", totalFootprint);
        System.out.printf("  Promedio por objeto: %,.2f kgCO₂/año%n",
                          totalFootprint / entities.size());
        System.out.println(LINE);
        System.out.println();
    }

    /**
     * Persiste el reporte en un archivo de texto usando CarbonFootprintStorage.
     *
     * @param entities lista de objetos CarbonFootprint
     */
    public static void persistReport(List<CarbonFootprint> entities) {
        CarbonFootprintStorage storage = new CarbonFootprintStorage();
        try {
            storage.writeToFile(entities, OUTPUT_FILE);
            System.out.printf("  ✔ Reporte guardado en: %s%n%n", OUTPUT_FILE);
        } catch (IOException e) {
            System.err.println("  ✘ Error al guardar el reporte: " + e.getMessage());
        }
    }

    /**
     * Lee el archivo persistido y muestra el número de líneas leídas.
     */
    private static void verifyFile() {
        CarbonFootprintStorage storage = new CarbonFootprintStorage();
        try {
            List<String> lines = storage.readFromFile(OUTPUT_FILE);
            System.out.printf("  ✔ Archivo leído correctamente: %d líneas encontradas.%n",
                              lines.size());

            List<String> footprintLines = storage.extractFootprintLines(lines);
            System.out.printf("  ✔ Registros de huella de carbono en archivo: %d%n%n",
                              footprintLines.size());
        } catch (IOException e) {
            System.err.println("  ✘ Error al leer el archivo: " + e.getMessage());
        }
    }
}
