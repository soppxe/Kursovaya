package model;

/**
 * Модель данных для хранения результатов расчета параметров машины непрерывного литья заготовок (МНЛЗ).
 * Содержит все расчетные и исходные параметры, необходимые для проектирования и настройки МНЛЗ.
 *
 * <p><b>Основное назначение:</b>
 * <ul>
 * <li>Хранение геометрических параметров сечения заготовки</li>
 * <li>Сохранение расчетных параметров МНЛЗ (число ручьев, металлургическая длина)</li>
 * <li>Запись характеристик разливки (скорость, масса)</li>
 * <li>Хранение физических свойств стали и коэффициентов для расчетов</li>
 * </ul>
 *
 * <p><b>Использование в расчетах:</b>
 * <ul>
 * <li>Проектирование новых машин непрерывного литья</li>
 * <li>Оптимизация параметров существующих МНЛЗ</li>
 * <li>Анализ технологических возможностей оборудования</li>
 * <li>Формирование отчетов и технической документации</li>
 * </ul>
 *
 * @author Ваше имя
 * @version 1.0
 * @since 2024
 */
public class CasterResult {

    /**
     * Уникальный идентификатор результата расчета в базе данных.
     * Используется для однозначной идентификации и связи с другими сущностями.
     */
    private int id;

    /**
     * Марка разливаемой стали.
     * Определяет технологические особенности процесса разливки.
     */
    private String steelGrade;

    /**
     * Масса разливаемой стали в тоннах.
     * Используется для расчета производительности МНЛЗ.
     */
    private double castingWeight;

    /**
     * Ширина поперечного сечения заготовки в миллиметрах.
     * Основной геометрический параметр для расчета числа ручьев.
     */
    private double sectionWidth;

    /**
     * Толщина поперечного сечения заготовки в миллиметрах.
     * Критический параметр для расчета металлургической длины.
     */
    private double sectionThickness;

    /**
     * Расчетное число ручьев МНЛЗ.
     * Определяет производительность машины и компоновку оборудования.
     */
    private int numberOfStreams;

    /**
     * Металлургическая длина МНЛЗ в метрах.
     * Расстояние от мениска до полного затвердевания заготовки.
     */
    private double metallurgicalLength;

    /**
     * Радиус кривизны МНЛЗ радиального типа в метрах.
     * Определяет габариты и компоновку машины.
     */
    private double machineRadius;

    /**
     * Высота МНЛЗ в метрах.
     * Общий габаритный размер машины по вертикали.
     */
    private double machineHeight;

    /**
     * Скорость разливки в метрах в минуту.
     * Основной технологический параметр процесса непрерывной разливки.
     */
    private double castingSpeed;

    /**
     * Плотность жидкой стали в килограммах на кубический метр.
     * Используется в расчетах массового расхода металла.
     */
    private double steelDensity;

    /**
     * Коэффициент металлургической длины.
     * Эмпирический коэффициент, зависящий от марки стали и условий разливки.
     */
    private double metallurgicalCoef;

    /**
     * Конструктор по умолчанию.
     * Создает экземпляр результата расчета с параметрами по умолчанию.
     */
    public CasterResult() {}

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
     * @param id идентификатор результата расчета
     */
    public void setId(int id) { this.id = id; }

    /**
     * Возвращает марку разливаемой стали.
     *
     * @return марка стали
     */
    public String getSteelGrade() { return steelGrade; }

    /**
     * Устанавливает марку разливаемой стали.
     *
     * @param steelGrade марка стали
     */
    public void setSteelGrade(String steelGrade) { this.steelGrade = steelGrade; }

    /**
     * Возвращает массу разливаемой стали.
     *
     * @return масса разливаемой стали в тоннах
     */
    public double getCastingWeight() { return castingWeight; }

    /**
     * Устанавливает массу разливаемой стали.
     *
     * @param castingWeight масса разливаемой стали в тоннах
     */
    public void setCastingWeight(double castingWeight) { this.castingWeight = castingWeight; }

    /**
     * Возвращает ширину поперечного сечения заготовки.
     *
     * @return ширина сечения в миллиметрах
     */
    public double getSectionWidth() { return sectionWidth; }

    /**
     * Устанавливает ширину поперечного сечения заготовки.
     *
     * @param sectionWidth ширина сечения в миллиметрах
     */
    public void setSectionWidth(double sectionWidth) { this.sectionWidth = sectionWidth; }

    /**
     * Возвращает толщину поперечного сечения заготовки.
     *
     * @return толщина сечения в миллиметрах
     */
    public double getSectionThickness() { return sectionThickness; }

    /**
     * Устанавливает толщину поперечного сечения заготовки.
     *
     * @param sectionThickness толщина сечения в миллиметрах
     */
    public void setSectionThickness(double sectionThickness) { this.sectionThickness = sectionThickness; }

    /**
     * Возвращает расчетное число ручьев МНЛЗ.
     *
     * @return число ручьев МНЛЗ
     */
    public int getNumberOfStreams() { return numberOfStreams; }

    /**
     * Устанавливает расчетное число ручьев МНЛЗ.
     *
     * @param numberOfStreams число ручьев МНЛЗ
     */
    public void setNumberOfStreams(int numberOfStreams) { this.numberOfStreams = numberOfStreams; }

    /**
     * Возвращает металлургическую длину МНЛЗ.
     *
     * @return металлургическая длина в метрах
     */
    public double getMetallurgicalLength() { return metallurgicalLength; }

    /**
     * Устанавливает металлургическую длину МНЛЗ.
     *
     * @param metallurgicalLength металлургическая длина в метрах
     */
    public void setMetallurgicalLength(double metallurgicalLength) { this.metallurgicalLength = metallurgicalLength; }

    /**
     * Возвращает радиус кривизны МНЛЗ.
     *
     * @return радиус МНЛЗ в метрах
     */
    public double getMachineRadius() { return machineRadius; }

    /**
     * Устанавливает радиус кривизны МНЛЗ.
     *
     * @param machineRadius радиус МНЛЗ в метрах
     */
    public void setMachineRadius(double machineRadius) { this.machineRadius = machineRadius; }

    /**
     * Возвращает высоту МНЛЗ.
     *
     * @return высота МНЛЗ в метрах
     */
    public double getMachineHeight() { return machineHeight; }

    /**
     * Устанавливает высоту МНЛЗ.
     *
     * @param machineHeight высота МНЛЗ в метрах
     */
    public void setMachineHeight(double machineHeight) { this.machineHeight = machineHeight; }

    /**
     * Возвращает скорость разливки.
     *
     * @return скорость разливки в метрах в минуту
     */
    public double getCastingSpeed() { return castingSpeed; }

    /**
     * Устанавливает скорость разливки.
     *
     * @param castingSpeed скорость разливки в метрах в минуту
     */
    public void setCastingSpeed(double castingSpeed) { this.castingSpeed = castingSpeed; }

    /**
     * Возвращает плотность жидкой стали.
     *
     * @return плотность стали в килограммах на кубический метр
     */
    public double getSteelDensity() { return steelDensity; }

    /**
     * Устанавливает плотность жидкой стали.
     *
     * @param steelDensity плотность стали в килограммах на кубический метр
     */
    public void setSteelDensity(double steelDensity) { this.steelDensity = steelDensity; }

    /**
     * Возвращает коэффициент металлургической длины.
     *
     * @return коэффициент металлургической длины
     */
    public double getMetallurgicalCoef() { return metallurgicalCoef; }

    /**
     * Устанавливает коэффициент металлургической длины.
     *
     * @param metallurgicalCoef коэффициент металлургической длины
     */
    public void setMetallurgicalCoef(double metallurgicalCoef) { this.metallurgicalCoef = metallurgicalCoef; }
}