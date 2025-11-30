package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Модель данных для хранения результатов расчета раскисления и легирования стали.
 * Содержит полную информацию о исходных данных, целевых параметрах и результатах расчета.
 *
 * <p><b>Основное назначение:</b>
 * <ul>
 * <li>Хранение исходных данных для расчета (марка стали, начальный состав, масса)</li>
 * <li>Сохранение целевых параметров стали</li>
 * <li>Запись результатов расчета (добавленные материалы, конечный состав)</li>
 * <li>Использование для отображения результатов и сохранения в базу данных</li>
 * </ul>
 *
 * <p><b>Структура данных:</b>
 * <ul>
 * <li>Идентификатор расчета</li>
 * <li>Марка стали</li>
 * <li>Начальная масса и химический состав</li>
 * <li>Целевой химический состав</li>
 * <li>Добавленные материалы и их количество</li>
 * <li>Конечный химический состав</li>
 * <li>Количество углеродной добавки</li>
 * </ul>
 *
 * @author Ваше имя
 * @version 1.0
 * @see HashMap
 * @see Map
 * @since 2024
 */
public class AlloyingResult {

    /**
     * Уникальный идентификатор результата расчета в базе данных.
     * Используется для однозначной идентификации записи.
     */
    private int id;

    /**
     * Марка стали, для которой выполнен расчет.
     * Определяет требования к химическому составу и свойствам материала.
     */
    private String steelGrade;

    /**
     * Начальная масса металлической шихты в килограммах.
     * Используется как основа для всех расчетов добавления материалов.
     */
    private double initialWeight;

    /**
     * Начальный химический состав стали перед раскислением.
     * Формат: Map<Название элемента, Содержание в процентах>
     *
     * <p><b>Пример:</b>
     * <pre>
     * {
     *   "C": 0.15,
     *   "Mn": 0.25,
     *   "Si": 0.10,
     *   "P": 0.025,
     *   "S": 0.020
     * }
     * </pre>
     */
    private Map<String, Double> initialComposition;

    /**
     * Целевой химический состав стали после раскисления.
     * Определяет требуемые свойства готовой стали согласно марке.
     * Формат: Map<Название элемента, Содержание в процентах>
     */
    private Map<String, Double> targetComposition;

    /**
     * Материалы, добавленные в процессе раскисления и легирования.
     * Формат: Map<Название материала, Масса в килограммах>
     *
     * <p><b>Примеры материалов:</b>
     * <ul>
     * <li>Ферромарганец</li>
     * <li>Ферросилиций</li>
     * <li>Феррохром</li>
     * <li>Алюминий</li>
     * <li>Известняк</li>
     * </ul>
     */
    private Map<String, Double> addedMaterials;

    /**
     * Расчетный конечный химический состав стали после всех добавлений.
     * Формат: Map<Название элемента, Содержание в процентах>
     * Рассчитывается на основе начального состава и добавленных материалов.
     */
    private Map<String, Double> finalComposition;

    /**
     * Количество углеродной добавки в килограммах.
     * Используется для корректировки содержания углерода в стали.
     * Может быть положительным (добавление) или отрицательным (удаление).
     */
    private double carbonAdditive;

    /**
     * Конструктор по умолчанию.
     * Инициализирует все карты составов пустыми HashMap.
     *
     * <p><b>Инициализирует:</b>
     * <ul>
     * <li>{@link #initialComposition} - начальный состав</li>
     * <li>{@link #targetComposition} - целевой состав</li>
     * <li>{@link #addedMaterials} - добавленные материалы</li>
     * <li>{@link #finalComposition} - конечный состав</li>
     * </ul>
     */
    public AlloyingResult() {
        this.initialComposition = new HashMap<>();
        this.targetComposition = new HashMap<>();
        this.addedMaterials = new HashMap<>();
        this.finalComposition = new HashMap<>();
    }

    // ГЕТТЕРЫ И СЕТТЕРЫ

    /**
     * Возвращает уникальный идентификатор результата расчета.
     *
     * @return идентификатор результата расчета
     */
    public int getId() { return id; }

    /**
     * Устанавливает уникальный идентификатор результата расчета.
     *
     * @param id идентификатор результата расчета, должен быть положительным числом
     * @throws IllegalArgumentException если id меньше или равен 0
     */
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Идентификатор должен быть положительным числом");
        }
        this.id = id;
    }

    /**
     * Возвращает марку стали для которой выполнен расчет.
     *
     * @return марка стали
     */
    public String getSteelGrade() { return steelGrade; }

    /**
     * Устанавливает марку стали для расчета.
     *
     * @param steelGrade марка стали, не может быть null или пустой строкой
     * @throws IllegalArgumentException если steelGrade null или пустая строка
     */
    public void setSteelGrade(String steelGrade) {
        if (steelGrade == null || steelGrade.trim().isEmpty()) {
            throw new IllegalArgumentException("Марка стали не может быть пустой");
        }
        this.steelGrade = steelGrade;
    }

    /**
     * Возвращает начальную массу металлической шихты.
     *
     * @return начальная масса в килограммах
     */
    public double getInitialWeight() { return initialWeight; }

    /**
     * Устанавливает начальную массу металлической шихты.
     *
     * @param initialWeight начальная масса в килограммах, должна быть положительной
     * @throws IllegalArgumentException если initialWeight меньше или равен 0
     */
    public void setInitialWeight(double initialWeight) {
        if (initialWeight <= 0) {
            throw new IllegalArgumentException("Начальная масса должна быть положительной");
        }
        this.initialWeight = initialWeight;
    }

    /**
     * Возвращает начальный химический состав стали.
     *
     * @return карта начального химического состава (Элемент → Содержание в %)
     */
    public Map<String, Double> getInitialComposition() { return initialComposition; }

    /**
     * Устанавливает начальный химический состав стали.
     *
     * @param initialComposition карта начального химического состава
     * @throws IllegalArgumentException если initialComposition равен null
     */
    public void setInitialComposition(Map<String, Double> initialComposition) {
        if (initialComposition == null) {
            throw new IllegalArgumentException("Начальный состав не может быть null");
        }
        this.initialComposition = initialComposition;
    }

    /**
     * Возвращает целевой химический состав стали.
     *
     * @return карта целевого химического состава (Элемент → Содержание в %)
     */
    public Map<String, Double> getTargetComposition() { return targetComposition; }

    /**
     * Устанавливает целевой химический состав стали.
     *
     * @param targetComposition карта целевого химического состава
     * @throws IllegalArgumentException если targetComposition равен null
     */
    public void setTargetComposition(Map<String, Double> targetComposition) {
        if (targetComposition == null) {
            throw new IllegalArgumentException("Целевой состав не может быть null");
        }
        this.targetComposition = targetComposition;
    }

    /**
     * Возвращает карту добавленных материалов.
     *
     * @return карта добавленных материалов (Материал → Масса в кг)
     */
    public Map<String, Double> getAddedMaterials() { return addedMaterials; }

    /**
     * Устанавливает карту добавленных материалов.
     *
     * @param addedMaterials карта добавленных материалов
     * @throws IllegalArgumentException если addedMaterials равен null
     */
    public void setAddedMaterials(Map<String, Double> addedMaterials) {
        if (addedMaterials == null) {
            throw new IllegalArgumentException("Добавленные материалы не могут быть null");
        }
        this.addedMaterials = addedMaterials;
    }

    /**
     * Возвращает расчетный конечный химический состав.
     *
     * @return карта конечного химического состава (Элемент → Содержание в %)
     */
    public Map<String, Double> getFinalComposition() { return finalComposition; }

    /**
     * Устанавливает расчетный конечный химический состав.
     *
     * @param finalComposition карта конечного химического состава
     * @throws IllegalArgumentException если finalComposition равен null
     */
    public void setFinalComposition(Map<String, Double> finalComposition) {
        if (finalComposition == null) {
            throw new IllegalArgumentException("Конечный состав не может быть null");
        }
        this.finalComposition = finalComposition;
    }

    /**
     * Возвращает количество углеродной добавки.
     *
     * @return количество углеродной добавки в килограммах
     */
    public double getCarbonAdditive() { return carbonAdditive; }

    /**
     * Устанавливает количество углеродной добавки.
     *
     * @param carbonAdditive количество углеродной добавки в килограммах
     */
    public void setCarbonAdditive(double carbonAdditive) {
        this.carbonAdditive = carbonAdditive;
    }
}