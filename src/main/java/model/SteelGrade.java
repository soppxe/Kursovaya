package model;

/**
 * Модель данных для представления химического состава марки стали по ГОСТ и другим стандартам.
 * Содержит полную информацию о содержании химических элементов в стали.
 *
 * <p><b>Основное назначение:</b>
 * <ul>
 * <li>Хранение стандартизированных химических составов марок стали</li>
 * <li>Использование в расчетах раскисления и легирования</li>
 * <li>Сравнение фактического и требуемого составов стали</li>
 * <li>Формирование справочника марок стали для приложения</li>
 * </ul>
 *
 * <p><b>Химические элементы:</b>
 * <ul>
 * <li>Углерод (C) - основной элемент, определяющий прочность и твердость</li>
 * <li>Марганец (Mn) - повышает прокаливаемость и прочность</li>
 * <li>Кремний (Si) - раскислитель, повышает упругость</li>
 * <li>Сера (S) - вредная примесь, снижает механические свойства</li>
 * <li>Фосфор (P) - вредная примесь, вызывает хладноломкость</li>
 * <li>Хром (Cr) - повышает коррозионную стойкость и прокаливаемость</li>
 * <li>Никель (Ni) - повышает вязкость и коррозионную стойкость</li>
 * <li>Молибден (Mo) - повышает красностойкость и прочность при высоких температурах</li>
 * <li>Алюминий (Al) - раскислитель, измельчает зерно</li>
 * </ul>
 *
 * <p><b>Единицы измерения:</b>
 * Все содержания элементов указаны в процентах по массе (%).
 *
 * @author Ваше имя
 * @version 1.0
 * @since 2024
 */
public class SteelGrade {

    /**
     * Уникальный идентификатор марки стали в базе данных.
     * Используется для однозначной идентификации записи.
     */
    private int id;

    /**
     * Наименование марки стали согласно ГОСТ, ISO или другим стандартам.
     * Примеры: "Ст3", "40Х", "12Х18Н10Т", "30ХГСА"
     */
    private String name;

    /**
     * Содержание углерода в процентах.
     * Основной легирующий элемент, определяющий класс стали.
     * Диапазон: 0.02% - 2.14%
     */
    private double carbon;

    /**
     * Содержание марганца в процентах.
     * Легирующий элемент, повышающий прокаливаемость.
     * Диапазон: 0.1% - 2.0%
     */
    private double manganese;

    /**
     * Содержание кремния в процентах.
     * Раскислитель, повышающий упругие свойства.
     * Диапазон: 0.1% - 3.0%
     */
    private double silicon;

    /**
     * Содержание серы в процентах.
     * Вредная примесь, ограничивается стандартами.
     * Диапазон: 0.005% - 0.06%
     */
    private double sulfur;

    /**
     * Содержание фосфора в процентах.
     * Вредная примесь, ограничивается стандартами.
     * Диапазон: 0.005% - 0.06%
     */
    private double phosphorus;

    /**
     * Содержание хрома в процентах.
     * Легирующий элемент для нержавеющих и инструментальных сталей.
     * Диапазон: 0.1% - 30%
     */
    private double chromium;

    /**
     * Содержание никеля в процентах.
     * Легирующий элемент для нержавеющих и конструкционных сталей.
     * Диапазон: 0.1% - 35%
     */
    private double nickel;

    /**
     * Содержание молибдена в процентах.
     * Легирующий элемент для жаропрочных и быстрорежущих сталей.
     * Диапазон: 0.05% - 10%
     */
    private double molybdenum;

    /**
     * Содержание алюминия в процентах.
     * Раскислитель, измельчитель зерна.
     * Диапазон: 0.01% - 1.0%
     */
    private double aluminum;

    /**
     * Конструктор по умолчанию.
     * Создает экземпляр марки стали с нулевыми значениями содержания элементов.
     * Используется при создании пустого объекта для последующего заполнения.
     */
    public SteelGrade() {}

    /**
     * Конструктор для создания марки стали с заданным химическим составом.
     * Используется для создания объектов, соответствующих стандартам ГОСТ.
     *
     * @param name наименование марки стали, не должно быть null
     * @param carbon содержание углерода в процентах
     * @param manganese содержание марганца в процентах
     * @param silicon содержание кремния в процентах
     * @param sulfur содержание серы в процентах
     * @param phosphorus содержание фосфора в процентах
     * @param chromium содержание хрома в процентах
     * @param nickel содержание никеля в процентах
     * @param molybdenum содержание молибдена в процентах
     * @param aluminum содержание алюминия в процентах
     */
    public SteelGrade(String name, double carbon, double manganese, double silicon,
                      double sulfur, double phosphorus, double chromium,
                      double nickel, double molybdenum, double aluminum) {
        this.name = name;
        this.carbon = carbon;
        this.manganese = manganese;
        this.silicon = silicon;
        this.sulfur = sulfur;
        this.phosphorus = phosphorus;
        this.chromium = chromium;
        this.nickel = nickel;
        this.molybdenum = molybdenum;
        this.aluminum = aluminum;
    }

    // ГЕТТЕРЫ И СЕТТЕРЫ

    /**
     * Возвращает уникальный идентификатор марки стали.
     *
     * @return идентификатор марки стали
     */
    public int getId() { return id; }

    /**
     * Устанавливает уникальный идентификатор марки стали.
     *
     * @param id идентификатор марки стали
     */
    public void setId(int id) { this.id = id; }

    /**
     * Возвращает наименование марки стали.
     *
     * @return наименование марки стали
     */
    public String getName() { return name; }

    /**
     * Устанавливает наименование марки стали.
     *
     * @param name наименование марки стали
     */
    public void setName(String name) { this.name = name; }

    /**
     * Возвращает содержание углерода в стали.
     *
     * @return содержание углерода в процентах
     */
    public double getCarbon() { return carbon; }

    /**
     * Устанавливает содержание углерода в стали.
     *
     * @param carbon содержание углерода в процентах
     */
    public void setCarbon(double carbon) { this.carbon = carbon; }

    /**
     * Возвращает содержание марганца в стали.
     *
     * @return содержание марганца в процентах
     */
    public double getManganese() { return manganese; }

    /**
     * Устанавливает содержание марганца в стали.
     *
     * @param manganese содержание марганца в процентах
     */
    public void setManganese(double manganese) { this.manganese = manganese; }

    /**
     * Возвращает содержание кремния в стали.
     *
     * @return содержание кремния в процентах
     */
    public double getSilicon() { return silicon; }

    /**
     * Устанавливает содержание кремния в стали.
     *
     * @param silicon содержание кремния в процентах
     */
    public void setSilicon(double silicon) { this.silicon = silicon; }

    /**
     * Возвращает содержание серы в стали.
     *
     * @return содержание серы в процентах
     */
    public double getSulfur() { return sulfur; }

    /**
     * Устанавливает содержание серы в стали.
     *
     * @param sulfur содержание серы в процентах
     */
    public void setSulfur(double sulfur) { this.sulfur = sulfur; }

    /**
     * Возвращает содержание фосфора в стали.
     *
     * @return содержание фосфора в процентах
     */
    public double getPhosphorus() { return phosphorus; }

    /**
     * Устанавливает содержание фосфора в стали.
     *
     * @param phosphorus содержание фосфора в процентах
     */
    public void setPhosphorus(double phosphorus) { this.phosphorus = phosphorus; }

    /**
     * Возвращает содержание хрома в стали.
     *
     * @return содержание хрома в процентах
     */
    public double getChromium() { return chromium; }

    /**
     * Устанавливает содержание хрома в стали.
     *
     * @param chromium содержание хрома в процентах
     */
    public void setChromium(double chromium) { this.chromium = chromium; }

    /**
     * Возвращает содержание никеля в стали.
     *
     * @return содержание никеля в процентах
     */
    public double getNickel() { return nickel; }

    /**
     * Устанавливает содержание никеля в стали.
     *
     * @param nickel содержание никеля в процентах
     */
    public void setNickel(double nickel) { this.nickel = nickel; }

    /**
     * Возвращает содержание молибдена в стали.
     *
     * @return содержание молибдена в процентах
     */
    public double getMolybdenum() { return molybdenum; }

    /**
     * Устанавливает содержание молибдена в стали.
     *
     * @param molybdenum содержание молибдена в процентах
     */
    public void setMolybdenum(double molybdenum) { this.molybdenum = molybdenum; }

    /**
     * Возвращает содержание алюминия в стали.
     *
     * @return содержание алюминия в процентах
     */
    public double getAluminum() { return aluminum; }

    /**
     * Устанавливает содержание алюминия в стали.
     *
     * @param aluminum содержание алюминия в процентах
     */
    public void setAluminum(double aluminum) { this.aluminum = aluminum; }
}