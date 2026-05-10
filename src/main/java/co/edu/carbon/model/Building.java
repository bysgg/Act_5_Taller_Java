package co.edu.carbon.model;

import co.edu.carbon.interfaces.CarbonFootprint;

/**
 * Clase Building — Edificio
 *
 * Representa un edificio residencial o comercial y calcula su huella de
 * carbono anual basándose en el consumo de gas natural y electricidad.
 *
 * Fórmula utilizada (EPA / IPCC):
 *   - Gas natural:    consumo_m3/año  × 2.204 kgCO₂/m³
 *   - Electricidad:   consumo_kWh/año × 0.233 kgCO₂/kWh  (factor Colombia / UPME)
 *
 * Atributos propios: nombre, número de pisos, área construida (m²),
 * consumo mensual de gas (m³) y consumo mensual de electricidad (kWh).
 *
 * @author Carbon Footprint App
 * @version 1.0
 */
public class Building implements CarbonFootprint {

    // ── Factores de emisión ────────────────────────────────────────────────
    /** kgCO₂ emitidos por cada m³ de gas natural quemado */
    private static final double GAS_EMISSION_FACTOR = 2.204;

    /** kgCO₂ emitidos por cada kWh eléctrico consumido (red colombiana) */
    private static final double ELECTRICITY_EMISSION_FACTOR = 0.233;

    // ── Atributos propios ──────────────────────────────────────────────────
    private final String name;
    private final int    floors;
    private final double builtAreaM2;
    private final double monthlyGasM3;
    private final double monthlyElectricityKwh;

    /**
     * Constructor completo de Building.
     *
     * @param name                  nombre o dirección del edificio
     * @param floors                número de pisos
     * @param builtAreaM2           área construida total en metros cuadrados
     * @param monthlyGasM3          consumo mensual promedio de gas natural (m³)
     * @param monthlyElectricityKwh consumo mensual promedio de electricidad (kWh)
     */
    public Building(String name, int floors, double builtAreaM2,
                    double monthlyGasM3, double monthlyElectricityKwh) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre del edificio no puede estar vacío.");
        if (floors <= 0)
            throw new IllegalArgumentException("El número de pisos debe ser positivo.");
        if (builtAreaM2 <= 0)
            throw new IllegalArgumentException("El área construida debe ser positiva.");
        if (monthlyGasM3 < 0)
            throw new IllegalArgumentException("El consumo de gas no puede ser negativo.");
        if (monthlyElectricityKwh < 0)
            throw new IllegalArgumentException("El consumo de electricidad no puede ser negativo.");

        this.name                  = name;
        this.floors                = floors;
        this.builtAreaM2           = builtAreaM2;
        this.monthlyGasM3          = monthlyGasM3;
        this.monthlyElectricityKwh = monthlyElectricityKwh;
    }

    // ── Implementación de CarbonFootprint ─────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * Cálculo:
     *   huella = (gasAnual × GAS_FACTOR) + (electricidadAnual × ELEC_FACTOR)
     */
    @Override
    public double getCarbonFootprint() {
        double annualGas         = monthlyGasM3          * 12;
        double annualElectricity = monthlyElectricityKwh * 12;
        return (annualGas * GAS_EMISSION_FACTOR)
             + (annualElectricity * ELECTRICITY_EMISSION_FACTOR);
    }

    /** {@inheritDoc} */
    @Override
    public String getIdentifierInfo() {
        return String.format(
            "[Edificio] Nombre: %-25s | Pisos: %2d | Área: %7.1f m²",
            name, floors, builtAreaM2
        );
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String getName()                   { return name; }
    public int    getFloors()                 { return floors; }
    public double getBuiltAreaM2()            { return builtAreaM2; }
    public double getMonthlyGasM3()           { return monthlyGasM3; }
    public double getMonthlyElectricityKwh()  { return monthlyElectricityKwh; }

    // ── toString ──────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format(
            "Building{name='%s', floors=%d, builtAreaM2=%.1f, " +
            "monthlyGasM3=%.1f, monthlyElectricityKwh=%.1f}",
            name, floors, builtAreaM2, monthlyGasM3, monthlyElectricityKwh
        );
    }
}
