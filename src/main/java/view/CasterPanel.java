package view;

import model.CasterResult;
import service.CalculatorService;
import service.DatabaseService;
import service.PDFExportService;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Панель для расчета параметров машины непрерывного литья заготовок (МНЛЗ) в приложении "Калькулятор металлурга".
 * Предоставляет пользовательский интерфейс для ввода параметров разливки, выполнения расчетов и управления результатами.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Ввод технологических параметров разливки (масса, сечение, скорость)</li>
 * <li>Выбор типа заготовки (сортовая, сляб) с автоматической настройкой параметров</li>
 * <li>Выбор марки стали с автоматической корректировкой скорости разливки</li>
 * <li>Выполнение расчета основных параметров МНЛЗ по формулам раздела 8</li>
 * <li>Отображение результатов расчета с рекомендациями по типу МНЛЗ</li>
 * <li>Сохранение результатов в базу данных</li>
 * <li>Экспорт результатов в текстовые отчеты</li>
 * <li>Просмотр рекомендованных скоростей разливки и математических формул</li>
 * </ul>
 *
 * <p><b>Рассчитываемые параметры МНЛЗ:</b>
 * <ul>
 * <li>Число ручьев (формула 8.2)</li>
 * <li>Металлургическая длина (формула 8.4)</li>
 * <li>Радиус МНЛЗ (формула 8.7)</li>
 * <li>Высота машины</li>
 * <li>Рекомендации по типу МНЛЗ на основе радиуса</li>
 * </ul>
 *
 * @author Саитова София
 * @version 1.0
 * @see JPanel
 * @see CasterResult
 * @see CalculatorService
 * @see DatabaseService
 * @since 2025
 */
public class CasterPanel extends JPanel {
    /**
     * Поле для ввода массы плавки в тоннах.
     */
    private JTextField weightField;

    /**
     * Выпадающий список для выбора марки стали.
     */
    private JComboBox<String> steelGradeCombo;

    /**
     * Выпадающий список для выбора типа заготовки (сортовая, сляб).
     */
    private JComboBox<String> sectionTypeCombo;

    /**
     * Поле для ввода ширины сечения заготовки в метрах.
     */
    private JTextField widthField;

    /**
     * Поле для ввода толщины сечения заготовки в метрах.
     */
    private JTextField thicknessField;

    /**
     * Поле для ввода скорости разливки в метрах в минуту.
     */
    private JTextField speedField;

    /**
     * Поле для ввода цикла разливки в минутах.
     */
    private JTextField cycleTimeField;

    /**
     * Область для отображения результатов расчета.
     */
    private JTextArea resultArea;

    /**
     * Сервис для выполнения расчетов параметров МНЛЗ.
     */
    private CalculatorService calculator;

    /**
     * Имя текущего пользователя для идентификации при сохранении результатов.
     */
    private String currentUser;

    /**
     * Форматтер для форматирования числовых значений с двумя десятичными знаками.
     */
    private DecimalFormat df = new DecimalFormat("#.##");

    /**
     * Последний результат расчета для повторного использования при сохранении и экспорте.
     */
    private CasterResult lastResult;

    /**
     * Конструктор панели расчета параметров МНЛЗ.
     *
     * @param username имя пользователя для идентификации при сохранении результатов
     */
    public CasterPanel(String username) {
        this.currentUser = username;
        this.calculator = new CalculatorService();
        initializeUI();
        loadSteelGrades();
        setupEventListeners();
    }

    /**
     * Инициализирует пользовательский интерфейс панели.
     * Создает и размещает все компоненты: панель ввода, область результатов и панель кнопок.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Панель ввода данных
        JPanel inputPanel = createInputPanel();

        // Панель результатов
        resultArea = new JTextArea(20, 60);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Панель кнопок
        JPanel buttonPanel = createButtonPanel();

        // Добавление компонентов
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Создает панель ввода данных с полями для параметров разливки.
     *
     * @return JPanel с организованными полями ввода в табличном формате
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Параметры машины непрерывного литья заготовок (МНЛЗ)"));

        // Марка стали
        panel.add(new JLabel("Марка стали:"));
        steelGradeCombo = new JComboBox<>();
        panel.add(steelGradeCombo);

        // Тип сечения
        panel.add(new JLabel("Тип заготовки:"));
        sectionTypeCombo = new JComboBox<>(new String[]{"Сортовая", "Сляб"});
        panel.add(sectionTypeCombo);

        // Масса плавки
        panel.add(new JLabel("Масса плавки, т:"));
        weightField = new JTextField("140");
        panel.add(weightField);

        // Ширина сечения
        panel.add(new JLabel("Ширина сечения, м:"));
        widthField = new JTextField("1.3");
        panel.add(widthField);

        // Толщина сечения
        panel.add(new JLabel("Толщина сечения, м:"));
        thicknessField = new JTextField("0.16");
        panel.add(thicknessField);

        // Скорость разливки
        panel.add(new JLabel("Скорость разливки, м/мин:"));
        speedField = new JTextField("1.3");
        panel.add(speedField);

        // Цикл разливки
        panel.add(new JLabel("Цикл разливки, мин:"));
        cycleTimeField = new JTextField("60");
        panel.add(cycleTimeField);

        // Рекомендованные скорости
        JButton showSpeedsBtn = new JButton("Показать рекомендованные скорости");
        panel.add(showSpeedsBtn);

        showSpeedsBtn.addActionListener(e -> showRecommendedSpeeds());

        return panel;
    }

    /**
     * Создает панель с кнопками управления для выполнения операций с расчетами.
     *
     * @return JPanel с кнопками: Расчет, Сохранение, Экспорт, Очистка, Формулы
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        // СТАНДАРТНЫЙ НАБОР КНОПОК
        JButton calculateBtn = new JButton("Рассчитать");
        JButton saveBtn = new JButton("Сохранить");
        JButton exportBtn = new JButton("Экспорт в PDF");
        JButton clearBtn = new JButton("Очистить");
        JButton formulasBtn = new JButton("Показать формулы");

        panel.add(calculateBtn);
        panel.add(saveBtn);
        panel.add(exportBtn);
        panel.add(clearBtn);
        panel.add(formulasBtn);

        // Обработчики событий
        calculateBtn.addActionListener(e -> calculate());
        saveBtn.addActionListener(e -> saveToDatabase());
        exportBtn.addActionListener(e -> exportToPDF());
        clearBtn.addActionListener(e -> clearResults());
        formulasBtn.addActionListener(e -> showFormulas());

        return panel;
    }

    /**
     * Настраивает обработчики событий для выпадающих списков.
     * Автоматически заполняет параметры при выборе типа заготовки и корректирует скорость при выборе марки стали.
     */
    private void setupEventListeners() {
        sectionTypeCombo.addActionListener(e -> {
            String selectedType = (String) sectionTypeCombo.getSelectedItem();
            if ("Сортовая".equals(selectedType)) {
                widthField.setText("0.2");
                thicknessField.setText("0.2");
                speedField.setText("2.0");
            } else if ("Сляб".equals(selectedType)) {
                widthField.setText("1.3");
                thicknessField.setText("0.16");
                speedField.setText("1.3");
            }
        });

        steelGradeCombo.addActionListener(e -> {
            String selectedGrade = (String) steelGradeCombo.getSelectedItem();
            if (selectedGrade != null) {
                if (selectedGrade.contains("35") || selectedGrade.contains("Ст3")) {
                    speedField.setText("1.3");
                } else if (selectedGrade.contains("70")) {
                    speedField.setText("0.9");
                }
            }
        });
    }

    /**
     * Загружает список марок стали в выпадающий список.
     * Добавляет основные марки стали и загружает дополнительные из базы данных.
     */
    private void loadSteelGrades() {
        List<model.SteelGrade> grades = DatabaseService.getAllSteelGrades();
        steelGradeCombo.addItem("Ст3сп");
        steelGradeCombo.addItem("35ГС");
        steelGradeCombo.addItem("35");
        steelGradeCombo.addItem("70");

        for (model.SteelGrade grade : grades) {
            steelGradeCombo.addItem(grade.getName());
        }
    }

    /**
     * Выполняет расчет параметров МНЛЗ на основе введенных данных.
     * Включает валидацию входных данных, выполнение расчета и отображение результатов.
     *
     * <p><b>Процесс расчета:</b>
     * <ol>
     * <li>Парсинг и валидация входных параметров</li>
     * <li>Проверка положительности всех значений</li>
     * <li>Выполнение расчета через CalculatorService</li>
     * <li>Отображение результатов с рекомендациями</li>
     * </ol>
     *
     * @throws NumberFormatException если введены некорректные числовые значения
     * @throws IllegalArgumentException если данные не проходят валидацию
     */
    private void calculate() {
        try {
            String steelGrade = (String) steelGradeCombo.getSelectedItem();
            double weight = Double.parseDouble(weightField.getText());
            double width = Double.parseDouble(widthField.getText());
            double thickness = Double.parseDouble(thicknessField.getText());
            double speed = Double.parseDouble(speedField.getText());
            int cycleTime = Integer.parseInt(cycleTimeField.getText());

            if (weight <= 0 || width <= 0 || thickness <= 0 || speed <= 0 || cycleTime <= 0) {
                JOptionPane.showMessageDialog(this, "Все значения должны быть положительными!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            lastResult = calculator.calculateCaster(steelGrade, weight, width, thickness, speed, cycleTime);
            displayResults(lastResult);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Проверьте правильность введенных числовых значений!\n" +
                            "Используйте точку как разделитель дробной части.",
                    "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Ошибка при расчете: " + e.getMessage(),
                    "Ошибка расчета", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Отображает результаты расчета параметров МНЛЗ в текстовой области.
     * Форматирует вывод с разделами: исходные данные, расчетные параметры, рекомендации.
     *
     * @param result объект с результатами расчета для отображения
     */
    private void displayResults(CasterResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("РАСЧЕТ ОСНОВНЫХ ПАРАМЕТРОВ МНЛЗ\n");
        sb.append("================================\n\n");

        sb.append("ИСХОДНЫЕ ДАННЫЕ:\n");
        sb.append("----------------\n");
        sb.append(String.format("Марка стали: %s\n", result.getSteelGrade()));
        sb.append(String.format("Плотность ρ: %.0f кг/м³\n", result.getSteelDensity()));
        sb.append(String.format("Коэф. kз: %.0f\n", result.getMetallurgicalCoef()));
        sb.append(String.format("Масса плавки: %.1f т\n", result.getCastingWeight()));
        sb.append(String.format("Сечение заготовки: %.2f × %.2f м\n", result.getSectionWidth(), result.getSectionThickness()));
        sb.append(String.format("Скорость разливки: %.2f м/мин\n", result.getCastingSpeed()));
        sb.append(String.format("Цикл разливки: %d мин\n", 60));

        sb.append("\nРАСЧЕТНЫЕ ПАРАМЕТРЫ МНЛЗ:\n");
        sb.append("-------------------------\n");
        sb.append(String.format("Число ручьев: %d\n", result.getNumberOfStreams()));
        sb.append(String.format("Металлургическая длина: %.2f м\n", result.getMetallurgicalLength()));
        sb.append(String.format("Радиус МНЛЗ: %.2f м\n", result.getMachineRadius()));
        sb.append(String.format("Высота МНЛЗ: %.2f м\n", result.getMachineHeight()));

        sb.append("\nРЕКОМЕНДАЦИИ:\n");
        sb.append("------------\n");

        double radius = result.getMachineRadius();
        if (radius <= 8) {
            sb.append("Тип МНЛЗ: Вертикальная\n");
            sb.append("Макс. протяженность жидкой фазы: 16 м\n");
        } else if (radius <= 12) {
            sb.append("Тип МНЛЗ: Радиальная с полным затвердеванием\n");
            sb.append("Макс. протяженность жидкой фазы: 19 м\n");
        } else if (radius <= 20) {
            sb.append("Тип МНЛЗ: Радиальная с двухфазным состоянием\n");
            sb.append("Макс. протяженность жидкой фазы: 30 м\n");
        } else {
            sb.append("Тип МНЛЗ: Криволинейная с дифференцированным выпрямлением\n");
            sb.append("Макс. протяженность жидкой фазы: 35 м\n");
        }

        double minRadius = 42 * result.getSectionThickness();
        if (result.getMachineRadius() < minRadius) {
            sb.append(String.format("\n⚠ ВНИМАНИЕ: Радиус меньше минимально допустимого (%.2f м)\n", minRadius));
            sb.append("Рекомендуется увеличить радиус для предотвращения трещин\n");
        }

        resultArea.setText(sb.toString());
    }

    /**
     * Отображает диалоговое окно с рекомендованными скоростями разливки для различных типов заготовок.
     * Содержит данные для сортовых заготовок и слябов различных размеров и марок стали.
     */
    private void showRecommendedSpeeds() {
        String message = """
            РЕКОМЕНДОВАННЫЕ СКОРОСТИ РАЗЛИВКИ
            ================================
            
            СОРТОВЫЕ ЗАГОТОВКИ:
            • 80×80 мм: 5.3-5.5 м/мин
            • 100×100 мм: 5.0-5.3 м/мин  
            • 125×125 мм: 3.1-4.4 м/мин
            • 180×180 мм: 2.3-2.5 м/мин
            • 200×200 мм: 1.6-2.1 м/мин
            • 250×250 мм: 0.8-1.1 м/мин
            • 300×360 мм: 0.65-0.70 м/мин
            
            СЛЯБЫ (ширина 1100-1700 мм):
            • Низко- и среднеуглеродистая сталь:
              - Толщина 170 мм: 1.3-1.4 м/мин
              - Толщина 250 мм: 0.9-1.1 м/мин
              
            • Высокоуглеродистая сталь:
              - Толщина 170 мм: 0.9-1.1 м/мин
              - Толщина 250 мм: 0.8-0.9 м/мин
            
            МАКСИМАЛЬНАЯ ПРОДОЛЖИТЕЛЬНОСТЬ РАЗЛИВКИ:
            • 12 т: 40 мин    • 100 т: 75 мин
            • 25 т: 50 мин    • 160 т: 85 мин  
            • 50 т: 60 мин    • 200 т: 90 мин
            """;

        JTextArea textArea = new JTextArea(message, 25, 50);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(this, scrollPane, "Рекомендованные скорости разливки", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Отображает диалоговое окно с математическими формулами расчета параметров МНЛЗ.
     * Содержит основные формулы из раздела 8 учебного пособия.
     */
    private void showFormulas() {
        String formulas = """
            ОСНОВНЫЕ ФОРМУЛЫ РАСЧЕТА МНЛЗ
            ============================
            
            1. МАССОВАЯ СКОРОСТЬ РАЗЛИВКИ (8.1):
               Gр = M / (0.9 × n × τ_р)
               где: Gр - массовая скорость (т/мин)
                     M - садка сталеплавильного агрегата (т)
                     n - число ручьев
                     τ_р - цикл разливки (мин)
            
            2. ЧИСЛО РУЧЬЕВ (8.2):
               n = M / (B × b × ρ × v × τ_max)
               где: B, b - ширина и толщина заготовки (м)
                     ρ = 7300 кг/м³ - плотность стали
                     v - скорость разливки (м/мин)
                     τ_max - макс. продолжительность разливки (мин)
            
            3. МЕТАЛЛУРГИЧЕСКАЯ ДЛИНА (8.4):
               L_л = k_з × b² × v
               где: L_л - глубина лунки жидкой фазы (м)
                     k_з - коэффициент полного затвердевания
                     (340 - для слябов >1200 мм, 290 - <1200 мм, 240 - сортовая)
            
            4. РАДИУС МНЛЗ (8.7):
               R = 2 × L_л / π
               H = R (высота установки)
            
            5. МИНИМАЛЬНЫЙ РАДИУС (8.9):
               R_min ≥ 42 × b
            """;

        JTextArea textArea = new JTextArea(formulas, 20, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(this, scrollPane, "Формулы расчета МНЛЗ", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Сохраняет последний результат расчета в базу данных.
     * Требует предварительного выполнения расчета.
     */
    private void saveToDatabase() {
        if (lastResult == null) {
            JOptionPane.showMessageDialog(this,
                    "Сначала выполните расчет для сохранения!",
                    "Нет данных", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = DatabaseService.saveCasterResult(lastResult, currentUser);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Результаты успешно сохранены в базу данных!",
                    "Сохранение", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ошибка при сохранении в базу данных!",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Экспортирует последний результат расчета в текстовый файл формата PDF-like.
     * Требует предварительного выполнения расчета.
     */
    private void exportToPDF() {
        if (lastResult == null) {
            JOptionPane.showMessageDialog(this,
                    "Сначала выполните расчет для экспорта результатов!",
                    "Нет данных", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PDFExportService.exportCasterToPDF(lastResult, currentUser);
    }

    /**
     * Очищает область результатов предыдущего расчета.
     */
    private void clearResults() {
        resultArea.setText("");
    }
}