# Carbon Footprint App — Java
### Tarea Universitaria · Programación Orientada a Objetos

---

## Descripción

Aplicación en Java que demuestra **polimorfismo**, **modularidad**, **manejo de archivos** y **pruebas unitarias** mediante el cálculo de la huella de carbono anual de tres entidades no relacionadas por herencia:

| Clase        | Calcula huella a partir de…                              |
|--------------|----------------------------------------------------------|
| `Building`   | Consumo de gas natural y electricidad mensual            |
| `Car`        | Tipo de combustible, eficiencia y kilómetros anuales     |
| `Bicycle`    | Fabricación amortizada + calorías adicionales del ciclista |

---

## Estructura del proyecto

```
carbon_footprint/
├── pom.xml
└── src/
    ├── main/java/co/edu/carbon/
    │   ├── interfaces/
    │   │   └── CarbonFootprint.java      ← Interfaz principal
    │   ├── model/
    │   │   ├── Building.java
    │   │   ├── Car.java
    │   │   └── Bicycle.java
    │   ├── storage/
    │   │   └── CarbonFootprintStorage.java   ← Persistencia .txt
    │   └── app/
    │       └── Main.java                 ← Clase principal
    └── test/java/co/edu/carbon/
        ├── model/
        │   ├── BuildingTest.java         ← 14 pruebas
        │   ├── CarTest.java              ← 15 pruebas
        │   └── BicycleTest.java          ← 15 pruebas
        ├── storage/
        │   └── CarbonFootprintStorageTest.java  ← 18 pruebas
        └── app/
            └── MainTest.java             ← 5 pruebas
```

**Total: 67 pruebas unitarias**

---

## Conceptos de POO aplicados

| Concepto              | Dónde se aplica                                                                 |
|-----------------------|---------------------------------------------------------------------------------|
| **Interfaz**          | `CarbonFootprint` — contrato con `getCarbonFootprint()` y `getIdentifierInfo()` |
| **Polimorfismo**      | Loop sobre `ArrayList<CarbonFootprint>` invoca métodos en runtime               |
| **Encapsulamiento**   | Todos los atributos son `private final`; acceso solo por getters                |
| **Modularidad**       | Cuatro paquetes separados: `interfaces`, `model`, `storage`, `app`              |
| **Manejo de archivos**| `CarbonFootprintStorage` usa `BufferedWriter` / `BufferedReader`                |
| **SRP (SOLID)**       | Cada clase tiene una sola responsabilidad                                        |
| **ISP (SOLID)**       | La interfaz es mínima y específica                                               |
| **Validación**        | Constructores defensivos con `IllegalArgumentException`                         |

---

## Fórmulas de huella de carbono

### Building
```
huella = (gas_m3/mes × 12 × 2.204) + (kWh/mes × 12 × 0.233)
```
- Factor gas: 2.204 kgCO₂/m³ (IPCC)
- Factor electricidad: 0.233 kgCO₂/kWh (UPME Colombia)

### Car
```
litros/año = km_año / eficiencia_km_por_litro
huella     = litros/año × factor_combustible
```
| Combustible | Factor (kgCO₂/L) |
|-------------|-----------------|
| Gasolina    | 2.31            |
| Diésel      | 2.68            |
| GNV         | 1.96            |

### Bicycle
```
huella = (96 kgCO₂ / vida_útil_años) + (km_año × 25 kcal/km / 1000) × 0.16 kgCO₂
```
- 96 kgCO₂: fabricación promedio (Chester & Horvath, 2009)
- 0.16 kgCO₂/1000kcal: dieta promedio (EEB, 2011)

---

## Requisitos

- Java 17+
- Maven 3.8+

---

## Compilar y ejecutar

```bash
# Compilar
mvn compile

# Ejecutar pruebas
mvn test

# Empaquetar
mvn package

# Ejecutar
java -jar target/carbon-footprint.jar
```

---

## Ejemplo de salida

```
═══════════════════════════════════════════════════════════════════════
   CARBON FOOTPRINT — REPORTE DE HUELLA DE CARBONO ANUAL
═══════════════════════════════════════════════════════════════════════

  #1  [Edificio] Nombre: Torre Colpatria           | Pisos: 48 | Área: 28000.0 m²
       ↳ Huella de carbono: 1,107,720.00 kgCO₂/año

  #2  [Edificio] Nombre: Residencias El Prado      | Pisos:  6 | Área:  3200.0 m²
       ↳ Huella de carbono:  122,612.40 kgCO₂/año

  #3  [Auto]     2021 Toyota    Corolla       | Combustible: Gasolina      | 20000 km/año
       ↳ Huella de carbono:    3,422.22 kgCO₂/año

  ...

═══════════════════════════════════════════════════════════════════════
  Total de objetos   : 8
  Huella acumulada   : 1,308,496.37 kgCO₂/año
  Promedio por objeto: 163,562.05 kgCO₂/año
═══════════════════════════════════════════════════════════════════════
```

---

## Autor

Proyecto académico — Programación en Java  
Basado en: Deitel, P. y Deitel, H. — *How to Program in Java* (9th ed.), Prentice Hall.
