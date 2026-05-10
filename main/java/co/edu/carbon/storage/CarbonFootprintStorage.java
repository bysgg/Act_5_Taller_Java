package co.edu.carbon.storage;

import co.edu.carbon.interfaces.CarbonFootprint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CarbonFootprintStorage — Módulo de Persistencia
 *
 * Responsabilidad única (SRP): gestionar la escritura y lectura de
 * huellas de carbono en un archivo de texto plano (.txt).
 *
 * Formato del archivo:
 * ─────────────────────────────────────────────────────────────
 * CARBON FOOTPRINT REPORT — 2024-05-10 14:32:00
 * ─────────────────────────────────────────────────────────────
 * #1  [Edificio] Nombre: Torre Colpatria          | ...
 *     Huella de Carbono: 12 345,67 kgCO2/año
 *
 * #2  [Auto]     2020 Toyota   Corolla        | ...
 *     Huella de Carbono: 3 210,50 kgCO2/año
 * ─────────────────────────────────────────────────────────────
 * TOTAL OBJETOS: 3
 * HUELLA TOTAL:  15 876,17 kgCO2/año
 * ─────────────────────────────────────────────────────────────
 *
 * @author Carbon Footprint App
 * @version 1.0
 */
public class CarbonFootprintStorage {

    private static final String SEPARATOR =
        "─────────────────────────────────────────────────────────────────────────";
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── Escritura ──────────────────────────────────────────────────────────

    /**
     * Escribe la lista de objetos CarbonFootprint en un archivo de texto.
     *
     * @param footprints lista de objetos que implementan CarbonFootprint
     * @param filePath   ruta completa del archivo de salida (ej. "reporte.txt")
     * @throws IOException si ocurre un error de escritura
     */
    public void writeToFile(List<CarbonFootprint> footprints, String filePath)
            throws IOException {
        if (footprints == null)
            throw new IllegalArgumentException("La lista de huellas no puede ser null.");
        if (filePath == null || filePath.isBlank())
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía.");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writeHeader(writer);
            writeBody(writer, footprints);
            writeFooter(writer, footprints);
        }
    }

    /** Escribe el encabezado del reporte */
    private void writeHeader(BufferedWriter writer) throws IOException {
        writer.write(SEPARATOR);
        writer.newLine();
        writer.write(" CARBON FOOTPRINT REPORT — " +
                     LocalDateTime.now().format(FORMATTER));
        writer.newLine();
        writer.write(SEPARATOR);
        writer.newLine();
        writer.newLine();
    }

    /** Escribe cada objeto con su información y huella */
    private void writeBody(BufferedWriter writer, List<CarbonFootprint> footprints)
            throws IOException {
        int index = 1;
        for (CarbonFootprint fp : footprints) {
            writer.write(String.format("#%-3d %s", index, fp.getIdentifierInfo()));
            writer.newLine();
            writer.write(String.format("     Huella de Carbono: %,.2f kgCO₂/año",
                                       fp.getCarbonFootprint()));
            writer.newLine();
            writer.newLine();
            index++;
        }
    }

    /** Escribe el pie con totales */
    private void writeFooter(BufferedWriter writer, List<CarbonFootprint> footprints)
            throws IOException {
        double total = footprints.stream()
                                 .mapToDouble(CarbonFootprint::getCarbonFootprint)
                                 .sum();
        writer.write(SEPARATOR);
        writer.newLine();
        writer.write(String.format(" TOTAL OBJETOS : %d", footprints.size()));
        writer.newLine();
        writer.write(String.format(" HUELLA TOTAL  : %,.2f kgCO₂/año", total));
        writer.newLine();
        writer.write(SEPARATOR);
        writer.newLine();
    }

    // ── Lectura ────────────────────────────────────────────────────────────

    /**
     * Lee el archivo generado y devuelve sus líneas como lista de cadenas.
     * Permite verificar que la persistencia funcionó correctamente.
     *
     * @param filePath ruta completa del archivo a leer
     * @return lista con cada línea del archivo (sin saltos de línea)
     * @throws IOException si el archivo no existe o no puede leerse
     */
    public List<String> readFromFile(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank())
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía.");

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * Extrae del archivo leído solo las líneas que contienen huellas de carbono.
     * Útil para pruebas unitarias y validación rápida.
     *
     * @param lines lista de líneas devuelta por {@link #readFromFile(String)}
     * @return lista de líneas que contienen "Huella de Carbono:"
     */
    public List<String> extractFootprintLines(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (line.contains("Huella de Carbono:")) {
                result.add(line.trim());
            }
        }
        return result;
    }

    /**
     * Lee el archivo y devuelve su contenido como una sola cadena de texto.
     *
     * @param filePath ruta del archivo
     * @return contenido completo del archivo
     * @throws IOException si el archivo no puede leerse
     */
    public String readFileAsString(String filePath) throws IOException {
        List<String> lines = readFromFile(filePath);
        return String.join(System.lineSeparator(), lines);
    }
}
