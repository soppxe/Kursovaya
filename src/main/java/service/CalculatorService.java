package service;

import model.AlloyingResult;
import model.CasterResult;

import java.util.HashMap;
import java.util.Map;

public class CalculatorService {

    // Химический состав материалов из Таблицы 7.1
    private static final Map<String, Map<String, Double>> MATERIALS = new HashMap<>();
    private static final Map<String, Double> BURN_LOSS = new HashMap<>();

    static {
        // Инициализация материалов
        initMaterials();
        initBurnLoss();
    }

    private static void initMaterials() {
        // Ферромарганец ФМн78
        Map<String, Double> fmn = new HashMap<>();
        fmn.put("C", 7.0); fmn.put("Mn", 80.0); fmn.put("Si", 1.5);
        fmn.put("S", 0.03); fmn.put("P", 0.35); fmn.put("Fe", 10.6);
        MATERIALS.put("Ферромарганец ФМн78", fmn);

        // Ферросилиций ФС65
        Map<String, Double> fsi = new HashMap<>();
        fsi.put("Mn", 0.4); fsi.put("Si", 65.0); fsi.put("S", 0.02);
        fsi.put("P", 0.05); fsi.put("Cr", 0.4); fsi.put("Al", 2.5);
        fsi.put("Fe", 31.6);
        MATERIALS.put("Ферросилиций ФС65", fsi);

        // Феррохром ФХ100А
        Map<String, Double> fcr = new HashMap<>();
        fcr.put("C", 0.9); fcr.put("Si", 0.8); fcr.put("S", 0.02);
        fcr.put("P", 0.02); fcr.put("Cr", 68.0); fcr.put("Al", 0.2);
        fcr.put("Fe", 30.0);
        MATERIALS.put("Феррохром ФХ100А", fcr);

        // Алюминий первичный
        Map<String, Double> al = new HashMap<>();
        al.put("Si", 1.5); al.put("P", 1.5); al.put("Al", 97.0);
        MATERIALS.put("Алюминий первичный", al);

        // Науглероживатель
        Map<String, Double> carbon = new HashMap<>();
        carbon.put("C", 99.8); carbon.put("Si", 0.2);
        MATERIALS.put("Науглероживатель", carbon);
    }

    private static void initBurnLoss() {
        BURN_LOSS.put("Al", 40.0);
        BURN_LOSS.put("C", 40.0);
        BURN_LOSS.put("Si", 15.0);
        BURN_LOSS.put("Mn", 10.0);
        BURN_LOSS.put("Cr", 10.0);
        BURN_LOSS.put("Fe", 10.0);
        BURN_LOSS.put("Ni", 0.0);
        BURN_LOSS.put("Mo", 0.0);
    }

    /**
     * Расчет раскисления и легирования по формуле 7.1:
     * M = [M_ж.ст. * (C_гот.ст. - C_исх) * 100] / [C_фер * (100 - K_уг)]
     */
    public AlloyingResult calculateAlloying(double steelWeight,
                                            Map<String, Double> initialComp,
                                            Map<String, Double> targetComp,
                                            String steelGrade) {

        AlloyingResult result = new AlloyingResult();
        result.setSteelGrade(steelGrade);
        result.setInitialWeight(steelWeight);
        result.setInitialComposition(new HashMap<>(initialComp));
        result.setTargetComposition(new HashMap<>(targetComp));

        Map<String, Double> addedMaterials = new HashMap<>();
        Map<String, Double> finalComp = new HashMap<>(initialComp);

        // Расчет ферромарганца для марганца
        double mnNeeded = targetComp.get("Mn") - initialComp.get("Mn");
        if (mnNeeded > 0) {
            double feMn = calculateMaterialWeight(steelWeight, mnNeeded, "Ферромарганец ФМн78", "Mn");
            addedMaterials.put("Ферромарганец ФМн78", feMn);
            addMaterialContribution(finalComp, "Ферромарганец ФМн78", feMn, steelWeight);
        }

        // Расчет ферросилиция для кремния
        double siNeeded = targetComp.get("Si") - finalComp.get("Si");
        if (siNeeded > 0) {
            double feSi = calculateMaterialWeight(steelWeight, siNeeded, "Ферросилиций ФС65", "Si");
            addedMaterials.put("Ферросилиций ФС65", feSi);
            addMaterialContribution(finalComp, "Ферросилиций ФС65", feSi, steelWeight);
        }

        // Расчет алюминия
        double alNeeded = targetComp.get("Al") - finalComp.get("Al");
        if (alNeeded > 0) {
            double aluminum = calculateMaterialWeight(steelWeight, alNeeded, "Алюминий первичный", "Al");
            addedMaterials.put("Алюминий первичный", aluminum);
            addMaterialContribution(finalComp, "Алюминий первичный", aluminum, steelWeight);
        }

        // Корректировка углерода
        double carbonNeeded = targetComp.get("C") - finalComp.get("C");
        if (carbonNeeded > 0) {
            double carbonizer = calculateMaterialWeight(steelWeight, carbonNeeded, "Науглероживатель", "C");
            addedMaterials.put("Науглероживатель", carbonizer);
            result.setCarbonAdditive(carbonizer);
            addMaterialContribution(finalComp, "Науглероживатель", carbonizer, steelWeight);
        }

        result.setAddedMaterials(addedMaterials);
        result.setFinalComposition(finalComp);

        return result;
    }

    private double calculateMaterialWeight(double steelWeight, double elementNeeded,
                                           String materialName, String element) {
        Map<String, Double> material = MATERIALS.get(materialName);
        double elementInMaterial = material.get(element);
        double burnLoss = BURN_LOSS.getOrDefault(element, 0.0);

        // Формула 7.1: M = [M_ж.ст. * (C_гот.ст. - C_исх) * 100] / [C_фер * (100 - K_уг)]
        return (steelWeight * elementNeeded * 100) / (elementInMaterial * (100 - burnLoss));
    }

    private void addMaterialContribution(Map<String, Double> composition,
                                         String materialName, double materialWeight,
                                         double steelWeight) {
        Map<String, Double> material = MATERIALS.get(materialName);

        for (Map.Entry<String, Double> entry : material.entrySet()) {
            String element = entry.getKey();
            double percent = entry.getValue();
            double burnLoss = BURN_LOSS.getOrDefault(element, 0.0);

            // Количество элемента, вносимого материалом (с учетом угара)
            double elementAdded = (materialWeight * percent * (100 - burnLoss)) / 10000; // в кг
            // Пересчет в проценты
            double currentPercent = composition.getOrDefault(element, 0.0);
            composition.put(element, currentPercent + (elementAdded / steelWeight) * 100);
        }
    }

    /**
     * Расчет параметров МНЛЗ по формулам из раздела 8
     */
    public CasterResult calculateCaster(String steelGrade, double castingWeight,
                                        double width, double thickness,
                                        double castingSpeed, int cycleTime) {

        CasterResult result = new CasterResult();
        result.setSteelGrade(steelGrade);
        result.setCastingWeight(castingWeight);
        result.setSectionWidth(width);
        result.setSectionThickness(thickness);
        result.setCastingSpeed(castingSpeed);

        double steelDensity = 7300; // кг/м³

        // Формула 8.2: n = M / (B * b * ρ * v * τ_max)
        int streams = (int) Math.ceil((castingWeight * 1000) /
                (width * thickness * steelDensity * castingSpeed * cycleTime));
        result.setNumberOfStreams(streams);

        // Формула 8.4: L_л = k_з * b² * v
        // Формула 8.5: L_л = (k_з * b * M) / (0.9 * n * B * τ_р * ρ)
        double kz = (width > 1.2) ? 340 : (width < 1.2 && thickness < 0.3) ? 290 : 240;
        double metallurgicalLength = (kz * thickness * castingWeight * 1000) /
                (0.9 * streams * width * cycleTime * steelDensity);
        result.setMetallurgicalLength(metallurgicalLength);

        // Формула 8.7: R = 2 * L_л / π
        double radius = (2 * metallurgicalLength) / Math.PI;
        // Проверка минимального радиуса по формуле 8.9
        double minRadius = 42 * thickness;
        result.setMachineRadius(Math.max(radius, minRadius));
        result.setMachineHeight(result.getMachineRadius());

        return result;
    }
}