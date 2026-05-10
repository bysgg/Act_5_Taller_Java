package co.edu.carbon.model;

import co.edu.carbon.interfaces.CarbonFootprint;

/**
 * Clase Car — Automóvil
 *
 * Representa un vehículo de motor de combustión interna y calcula su
 * huella de carbono anual a partir del tipo de combustible, el consumo
 * y los kilómetros recorridos por año.
 *
 * Factores de emisión por litro quemado (IPCC / EEA):
 *   - Gasolina (petrol):  2.31 kgCO₂/litro
 *   - Diésel (diesel):    2.68 kgCO₂/litro
 *   - GNV (cng):          1.96 kgCO₂/litro equivalente
 *
 * Fórmula:
 *   litros_año = km_año / eficiencia_km_por_litro
 *   huella     = litros_año × factor_combustible
 *
 * Atributos propios: marca, modelo, año de fabricación, tipo de
 * combustible, eficiencia (km/L) y kilómetros anuales recorridos.
 *
 * @author Carbon Footprint App
 * @version 1.0
 */
public class Car implements CarbonFootprint {

    // ── Tipos de combustible permitidos ───────────────────────────────────
    public enum FuelType {
        GASOLINE(2.31, "Gasolina"),
        DIESEL  (2.68, "Diésel"),
        CNG     (1.96, "Gas Natural Vehicular");

        private final double emissionFactor;
        private final String label;

        FuelType(double emissionFactor, String label) {
            this.emissionFactor = emissionFactor;
            this.label          = label;
        }

        public double getEmissionFactor() { return emissionFactor; }
        public String getLabel()          { return label; }
    }

    // ── Atributos propios ──────────────────────────────────────────────────
    private final String   brand;
    private final String   model;
    private final int      year;
    private final FuelType fuelType;
    private final double   efficiencyKmPerLiter;
    private final double   annualKm;

    /**
     * Constructor completo de Car.
     *
     * @param brand                marca del vehículo (ej. "Toyota")
     * @param model                modelo del vehículo (ej. "Corolla")
     * @param year                 año de fabricación
     * @param fuelType             tipo de combustible ({@link FuelType})
     * @param efficiencyKmPerLiter eficiencia de combustible en km/litro
     * @param annualKm             kilómetros recorridos por año
     */
    public Car(String brand, String model, int year,
               FuelType fuelType, double efficiencyKmPerLiter, double annualKm) {
        if (brand == null || brand.isBlank())
            throw new IllegalArgumentException("La marca no puede estar vacía.");
        if (model == null || model.isBlank())
            throw new IllegalArgumentException("El modelo no puede estar vacío.");
        if (year < 1886 || year > 2100)
            throw new IllegalArgumentException("Año de fabricación inválido: " + year);
        if (fuelType == null)
            throw new IllegalArgumentException("El tipo de combustible es obligatorio.");
        if (efficiencyKmPerLiter <= 0)
            throw new IllegalArgumentException("La eficiencia debe ser mayor que cero.");
        if (annualKm < 0)
            throw new IllegalArgumentException("Los kilómetros anuales no pueden ser negativos.");

        this.brand                = brand;
        this.model                = model;
        this.year                 = year;
        this.fuelType             = fuelType;
        this.efficiencyKmPerLiter = efficiencyKmPerLiter;
        this.annualKm             = annualKm;
    }

    // ── Implementación de CarbonFootprint ─────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * Cálculo:
     *   litrosAño = annualKm / efficiencyKmPerLiter
     *   huella    = litrosAño × fuelType.emissionFactor
     */
    @Override
    public double getCarbonFootprint() {
        double litersPerYear = annualKm / efficiencyKmPerLiter;
        return litersPerYear * fuelType.getEmissionFactor();
    }

    /** {@inheritDoc} */
    @Override
    public String getIdentifierInfo() {
        return String.format(
            "[Auto]     %d %-10s %-12s | Combustible: %-25s | %.0f km/año",
            year, brand, model, fuelType.getLabel(), annualKm
        );
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String   getBrand()                 { return brand; }
    public String   getModel()                 { return model; }
    public int      getYear()                  { return year; }
    public FuelType getFuelType()              { return fuelType; }
    public double   getEfficiencyKmPerLiter()  { return efficiencyKmPerLiter; }
    public double   getAnnualKm()              { return annualKm; }

    // ── toString ──────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format(
            "Car{brand='%s', model='%s', year=%d, fuelType=%s, " +
            "efficiencyKmPerLiter=%.1f, annualKm=%.0f}",
            brand, model, year, fuelType.name(), efficiencyKmPerLiter, annualKm
        );
    }
}
