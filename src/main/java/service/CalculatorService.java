package service;

import model.AlloyingResult;
import model.CasterResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Основной сервис для выполнения расчетов в приложении "Калькулятор металлурга".
 * Содержит методы для расчета раскисления/легирования стали и параметров МНЛЗ.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Расчет расхода ферросплавов для раскисления и легирования стали</li>
 * <li>Расчет параметров машины непрерывного литья заготовок (МНЛЗ)</li>
 * <li>Учет угара химических элементов при легировании</li>
 * <li>Определение свойств стали по марке для точных расчетов</li>
 * </ul>
 *
 * <p><b>Используемые методики:</b>
 * <ul>
 * <li>Раздел 7 учебного пособия - раскисление и легирование</li>
 * <li>Раздел 8 учебного пособия - расчет параметров МНЛЗ</li>
 * <li>Таблица 7.1 - химический состав ферросплавов</li>
 * <li>Таблица 8.1 - свойства сталей по маркам</li>
 * </ul>
 *
 * @author Саитова София
 * @version 1.0
 * @see AlloyingResult
 * @see CasterResult
 * @since 2025
 */
public class CalculatorService {

    /**
     * База данных химических составов ферросплавов и добавок.
     * Данные соответствуют Таблице 7.1 учебного пособия.
     * Формат: Map<Название материала, Map<Элемент, Содержание в %>>
     */
    private static final Map<String, Map<String, Double>> MATERIALS = new HashMap<>();

    /**
     * Коэффициенты угара химических элементов при раскислении.
     * Выражены в процентах от вводимого количества элемента.
     */
    private static final Map<String, Double> BURN_LOSS = new HashMap<>();

    static {
        // Инициализация материалов
        initMaterials();
        initBurnLoss();
    }

    /**
     * Инициализирует базу данных химических составов материалов.
     * Данные взяты из Таблицы 7.1 учебного пособия.
     */
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

    /**
     * Инициализирует коэффициенты угара химических элементов.
     * Значения основаны на практическом опыте металлургических производств.
     */
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
     * Расчет раскисления и легирования стали по формуле 7.1 учебного пособия.
     * Формула 7.1: M = [M_ж.ст. * (C_гот.ст. - C_исх) * 100] / [C_фер * (100 - K_уг)]
     *
     * <p><b>Процесс расчета:</b>
     * <ol>
     * <li>Расчет ферромарганца для корректировки содержания марганца</li>
     * <li>Расчет ферросилиция для корректировки содержания кремния</li>
     * <li>Расчет алюминия для раскисления</li>
     * <li>Корректировка углерода при необходимости</li>
     * <li>Расчет конечного химического состава</li>
     * </ol>
     *
     * @param steelWeight масса жидкой стали в килограммах
     * @param initialComp начальный химический состав стали (Элемент → Содержание в %)
     * @param targetComp целевой химический состав стали (Элемент → Содержание в %)
     * @param steelGrade марка стали для идентификации
     * @return объект AlloyingResult с результатами расчета
     *
     * @throws IllegalArgumentException если параметры невалидны
     *
     * @see AlloyingResult
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

    /**
     * Расчет массы материала по формуле 7.1 учебного пособия.
     *
     * @param steelWeight масса жидкой стали в кг
     * @param elementNeeded необходимое количество элемента в %
     * @param materialName название ферросплава
     * @param element название химического элемента
     * @return масса материала в килограммах
     */
    private double calculateMaterialWeight(double steelWeight, double elementNeeded,
                                           String materialName, String element) {
        Map<String, Double> material = MATERIALS.get(materialName);
        double elementInMaterial = material.get(element);
        double burnLoss = BURN_LOSS.getOrDefault(element, 0.0);

        // Формула 7.1: M = [M_ж.ст. * (C_гот.ст. - C_исх) * 100] / [C_фер * (100 - K_уг)]
        return (steelWeight * elementNeeded * 100) / (elementInMaterial * (100 - burnLoss));
    }

    /**
     * Добавляет вклад материала в конечный химический состав стали.
     *
     * @param composition текущий химический состав
     * @param materialName название добавляемого материала
     * @param materialWeight масса материала в кг
     * @param steelWeight масса стали в кг
     */
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
     * Расчет параметров машины непрерывного литья заготовок (МНЛЗ) по разделу 8 учебного пособия.
     *
     * <p><b>Рассчитываемые параметры:</b>
     * <ul>
     * <li>Число ручьев (формула 8.2)</li>
     * <li>Металлургическая длина (формула 8.5)</li>
     * <li>Радиус МНЛЗ (формула 8.7)</li>
     * <li>Высота МНЛЗ</li>
     * </ul>
     *
     * @param steelGrade марка разливаемой стали
     * @param castingWeight масса разливаемой стали в тоннах
     * @param width ширина сечения заготовки в мм
     * @param thickness толщина сечения заготовки в мм
     * @param castingSpeed скорость разливки в м/мин
     * @param cycleTime время цикла разливки в минутах
     * @return объект CasterResult с расчетными параметрами МНЛЗ
     *
     * @see CasterResult
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

        // МАРКА СТАЛИ ОПРЕДЕЛЯЕТ ρ И kз! [web:107][web:109]
        SteelGradeProperties props = getSteelPropertiesByGrade(steelGrade);
        result.setSteelDensity(props.density);
        result.setMetallurgicalCoef(props.kz);

        // Формула 8.2: n = M / (B * b * ρ * v * τ_max)
        int streams = (int) Math.ceil((castingWeight * 1000) /
                (width * thickness * props.density * castingSpeed * cycleTime));
        result.setNumberOfStreams(streams);

        // Формула 8.4: L_л = k_з * b² * v
        // Формула 8.5: L_л = (k_з * b * M) / (0.9 * n * B * τ_р * ρ)
        double metallurgicalLength = (props.kz * thickness * castingWeight * 1000) /
                (0.9 * streams * width * cycleTime * props.density);
        result.setMetallurgicalLength(metallurgicalLength);

        // Формула 8.7: R = 2 * L_л / π
        double radius = (2 * metallurgicalLength) / Math.PI;
        // Проверка минимального радиуса по формуле 8.9
        double minRadius = 42 * thickness;
        result.setMachineRadius(Math.max(radius, minRadius));
        result.setMachineHeight(result.getMachineRadius());

        return result;
    }

    /**
     * Определяет свойства стали по марке согласно Таблице 8.1 учебного пособия.
     *
     * @param steelGrade марка стали
     * @return объект SteelGradeProperties с плотностью и коэффициентом kз
     */
    private SteelGradeProperties getSteelPropertiesByGrade(String steelGrade) {
        // Легированные/нержавейка (25Х2Н4МА, 12Х18Н10Т, 40ХГНМ...)
        if (steelGrade.contains("Х") || steelGrade.contains("Н") ||
                steelGrade.contains("4543-71") || steelGrade.contains("08Х")) {
            return new SteelGradeProperties(7200, 290);  // ρ=7200, kз=290 [web:107]
        }
        // Высокоуглеродистые (70, У8...)
        else if (steelGrade.contains("70") || steelGrade.contains("У") ||
                Double.parseDouble(steelGrade.replaceAll("[^0-9]", "")) > 50) {
            return new SteelGradeProperties(7400, 200);  // ρ=7400, kз=200
        }
        // Марганцево-кремнистые (35ГС, 25ХГСА...)
        else if (steelGrade.contains("ГС") || steelGrade.contains("ХГ")) {
            return new SteelGradeProperties(7250, 260);
        }
        // Обычные углеродистые (Ст3сп, 35, 45...)
        else {
            return new SteelGradeProperties(7300, 240);  // стандарт [web:109]
        }
    }

    /**
     * Вспомогательный класс для хранения свойств стали.
     * Используется для передачи плотности и коэффициента металлургической длины.
     */
    private static class SteelGradeProperties {
        /** Плотность стали в кг/м³ */
        final double density;
        /** Коэффициент металлургической длины */
        final double kz;

        /**
         * Конструктор для создания объекта свойств стали.
         *
         * @param density плотность стали в кг/м³
         * @param kz коэффициент металлургической длины
         */
        SteelGradeProperties(double density, double kz) {
            this.density = density;
            this.kz = kz;
        }
    }
}