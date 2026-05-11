package co.edu.carbon.interfaces;

/**
 * Interfaz CarbonFootprint
 *
 * Define el contrato que deben cumplir todas las entidades capaces de
 * calcular su huella de carbono anual (emisiones de CO₂ en kg/año).
 *
 * Principio de diseño: Interface Segregation (SOLID) — la interfaz es
 * pequeña y específica; cada clase la implementa de forma independiente
 * sin depender de métodos que no necesita.
 *
 */
public interface CarbonFootprint {

    /**
     * Calcula la huella de carbono anual del objeto.
     *
     * @return huella de carbono en kilogramos de CO₂ equivalente por año (kgCO₂/año)
     */
    double getCarbonFootprint();

    /**
     * Devuelve información identificadora del objeto para reportes.
     *
     * @return cadena descriptiva con los datos principales del objeto
     */
    String getIdentifierInfo();
}
