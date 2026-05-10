package co.edu.carbon.model;

import co.edu.carbon.interfaces.CarbonFootprint;

/**
 * Clase Bicycle — Bicicleta
 *
 * Representa una bicicleta y estima su huella de carbono anual.
 * Aunque las bicicletas no emiten CO₂ directamente, tienen una
 * huella de ciclo de vida asociada a:
 *   1. Fabricación del vehículo (amortizada en su vida útil).
 *   2. Calorías adicionales que consume el ciclista (producción alimentaria).
 *
 * Factores de referencia (estudios de ALC / Chester & Horvath, 2009):
 *   - Fabricación bicicleta: ~96 kgCO₂ por unidad (acero convencional)
 *                             repartidos en vida útil estimada de 15 años
 *   - Caloría adicional del ciclista: 0.16 kgCO₂ / 1000 kcal
 *     (dieta promedio occidental; fuente: Cycling vs Cars, EEB 2011)
 *   - Consumo energético por km en bicicleta: ~25 kcal / km
 *
 * Atributos propios: marca, tipo (urbana, MTB, ruta), peso (kg),
 * kilómetros anuales y vida útil esperada (años).
 *
 * @author Carbon Footprint App
 * @version 1.0
 */
public class Bicycle implements CarbonFootprint {

    // ── Constantes de ciclo de vida ────────────────────────────────────────
    /** kgCO₂ totales de fabricación de una bicicleta estándar */
    private static final double MANUFACTURING_KG_CO2   = 96.0;

    /** Calorías consumidas por el ciclista cada kilómetro */
    private static final double KCAL_PER_KM             = 25.0;

    /** kgCO₂ por cada 1 000 kcal de alimentación adicional */
    private static final double KG_CO2_PER_1000_KCAL    = 0.16;

    // ── Tipos de bicicleta ─────────────────────────────────────────────────
    public enum BikeType {
        URBAN  ("Urbana"),
        MTB    ("Montaña (MTB)"),
        ROAD   ("Ruta / Carretera"),
        FOLDING("Plegable"),
        CARGO  ("Carga");

        private final String label;
        BikeType(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    // ── Atributos propios ──────────────────────────────────────────────────
    private final String   brand;
    private final BikeType type;
    private final double   weightKg;
    private final double   annualKm;
    private final int      lifeExpectancyYears;

    /**
     * Constructor completo de Bicycle.
     *
     * @param brand                 marca de la bicicleta (ej. "Trek")
     * @param type                  tipo de bicicleta ({@link BikeType})
     * @param weightKg              peso del vehículo en kilogramos
     * @param annualKm              kilómetros recorridos por año
     * @param lifeExpectancyYears   vida útil estimada en años
     */
    public Bicycle(String brand, BikeType type, double weightKg,
                   double annualKm, int lifeExpectancyYears) {
        if (brand == null || brand.isBlank())
            throw new IllegalArgumentException("La marca no puede estar vacía.");
        if (type == null)
            throw new IllegalArgumentException("El tipo de bicicleta es obligatorio.");
        if (weightKg <= 0)
            throw new IllegalArgumentException("El peso debe ser mayor que cero.");
        if (annualKm < 0)
            throw new IllegalArgumentException("Los kilómetros anuales no pueden ser negativos.");
        if (lifeExpectancyYears <= 0)
            throw new IllegalArgumentException("La vida útil debe ser mayor que cero.");

        this.brand                = brand;
        this.type                 = type;
        this.weightKg             = weightKg;
        this.annualKm             = annualKm;
        this.lifeExpectancyYears  = lifeExpectancyYears;
    }

    // ── Implementación de CarbonFootprint ─────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * Cálculo:
     *   huellafabricacion = MANUFACTURING_KG_CO2 / lifeExpectancyYears
     *   huellaAlimentaria  = (annualKm × KCAL_PER_KM / 1000) × KG_CO2_PER_1000_KCAL
     *   huella total       = huellafabricacion + huellaAlimentaria
     */
    @Override
    public double getCarbonFootprint() {
        double manufacturingPerYear = MANUFACTURING_KG_CO2 / lifeExpectancyYears;
        double foodFootprint        = (annualKm * KCAL_PER_KM / 1000.0) * KG_CO2_PER_1000_KCAL;
        return manufacturingPerYear + foodFootprint;
    }

    /** {@inheritDoc} */
    @Override
    public String getIdentifierInfo() {
        return String.format(
            "[Bicicleta] Marca: %-12s | Tipo: %-20s | %.0f km/año",
            brand, type.getLabel(), annualKm
        );
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String   getBrand()                { return brand; }
    public BikeType getType()                 { return type; }
    public double   getWeightKg()             { return weightKg; }
    public double   getAnnualKm()             { return annualKm; }
    public int      getLifeExpectancyYears()  { return lifeExpectancyYears; }

    // ── toString ──────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format(
            "Bicycle{brand='%s', type=%s, weightKg=%.1f, " +
            "annualKm=%.0f, lifeExpectancyYears=%d}",
            brand, type.name(), weightKg, annualKm, lifeExpectancyYears
        );
    }
}
