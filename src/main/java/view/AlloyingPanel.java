package view;

import model.SteelGrade;
import model.AlloyingResult;
import service.CalculatorService;
import service.DatabaseService;
import service.PDFExportService;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Панель для расчета раскисления и легирования стали в приложении "Калькулятор металлурга".
 * Предоставляет пользовательский интерфейс для ввода данных, выполнения расчетов и управления результатами.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Ввод исходных данных для расчета (масса стали, химические составы)</li>
 * <li>Выбор марки стали из справочника с автоматической загрузкой целевых составов</li>
 * <li>Выполнение расчета раскисления и легирования по формулам раздела 7</li>
 * <li>Отображение результатов расчета в структурированном виде</li>
 * <li>Сохранение результатов в базу данных</li>
 * <li>Экспорт результатов в текстовые отчеты</li>
 * <li>Просмотр математических формул расчета</li>
 * </ul>
 *
 * <p><b>Поддерживаемые химические элементы:</b>
 * <ul>
 * <li>Углерод (C) - корректировка науглероживателем</li>
 * <li>Марганец (Mn) - легирование ферромарганцем</li>
 * <li>Кремний (Si) - легирование ферросилицием</li>
 * <li>Хром (Cr) - легирование феррохромом</li>
 * <li>Никель (Ni) - легирование техническим никелем</li>
 * <li>Молибден (Mo) - легирование ферромолибденом</li>
 * <li>Алюминий (Al) - раскисление первичным алюминием</li>
 * </ul>
 *
 * @author Ваше имя
 * @version 1.0
 * @see JPanel
 * @see AlloyingResult
 * @see CalculatorService
 * @see DatabaseService
 * @since 2024
 */
public class AlloyingPanel extends JPanel {
    /**
     * Поле для ввода массы жидкой стали в килограммах.
     */
    private JTextField weightField;

    /**
     * Выпадающий список для выбора марки стали из справочника.
     */
    private JComboBox<String> steelGradeCombo;

    /**
     * Поля для ввода начального и целевого содержания химических элементов.
     * Формат: [элемент]InitField - начальное содержание, [элемент]TargetField - целевое содержание.
     */
    private JTextField carbonInitField, carbonTargetField;
    private JTextField mnInitField, mnTargetField;
    private JTextField siInitField, siTargetField;
    private JTextField crInitField, crTargetField;
    private JTextField niInitField, niTargetField;
    private JTextField moInitField, moTargetField;
    private JTextField alTargetField;

    /**
     * Область для отображения результатов расчета.
     */
    private JTextArea resultArea;

    /**
     * Сервис для выполнения расчетов раскисления и легирования.
     */
    private CalculatorService calculator;

    /**
     * Имя текущего пользователя для идентификации при сохранении результатов.
     */
    private String currentUser;

    /**
     * Последний результат расчета для повторного использования при сохранении и экспорте.
     */
    private AlloyingResult lastResult;

    /**
     * Конструктор панели расчета раскисления.
     *
     * @param username имя пользователя для идентификации при сохранении результатов
     */
    public AlloyingPanel(String username) {
        this.currentUser = username;
        this.calculator = new CalculatorService();
        initializeUI();
        loadSteelGrades();
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
        resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Панель кнопок
        JPanel buttonPanel = createButtonPanel();

        // Добавление компонентов
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Обработчики событий
        steelGradeCombo.addActionListener(e -> loadSteelGradeData());
    }

    /**
     * Создает панель ввода данных с полями для массы стали и химических составов.
     *
     * @return JPanel с организованными полями ввода в табличном формате
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Исходные данные для расчета раскисления и легирования"));

        // Заголовки
        panel.add(new JLabel("Параметр"));
        panel.add(new JLabel("Начальный состав %"));
        panel.add(new JLabel("Целевой состав %"));
        panel.add(new JLabel("Материал"));

        // Масса стали
        panel.add(new JLabel("Масса стали, кг:"));
        weightField = new JTextField("100000");
        panel.add(weightField);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));

        // Марка стали
        panel.add(new JLabel("Марка стали:"));
        steelGradeCombo = new JComboBox<>();
        panel.add(steelGradeCombo);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));

        // Углерод
        panel.add(new JLabel("Углерод (C):"));
        carbonInitField = new JTextField("0.15");
        carbonTargetField = new JTextField("0.25");
        panel.add(carbonInitField);
        panel.add(carbonTargetField);
        panel.add(new JLabel("Науглероживатель"));

        // Марганец
        panel.add(new JLabel("Марганец (Mn):"));
        mnInitField = new JTextField("0.10");
        mnTargetField = new JTextField("0.40");
        panel.add(mnInitField);
        panel.add(mnTargetField);
        panel.add(new JLabel("Ферромарганец"));

        // Кремний
        panel.add(new JLabel("Кремний (Si):"));
        siInitField = new JTextField("0.0");
        siTargetField = new JTextField("0.28");
        panel.add(siInitField);
        panel.add(siTargetField);
        panel.add(new JLabel("Ферросилиций"));

        // Хром
        panel.add(new JLabel("Хром (Cr):"));
        crInitField = new JTextField("0.05");
        crTargetField = new JTextField("1.58");
        panel.add(crInitField);
        panel.add(crTargetField);
        panel.add(new JLabel("Феррохром"));

        // Никель
        panel.add(new JLabel("Никель (Ni):"));
        niInitField = new JTextField("0.10");
        niTargetField = new JTextField("4.30");
        panel.add(niInitField);
        panel.add(niTargetField);
        panel.add(new JLabel("Никель технич."));

        // Молибден
        panel.add(new JLabel("Молибден (Mo):"));
        moInitField = new JTextField("0.05");
        moTargetField = new JTextField("0.30");
        panel.add(moInitField);
        panel.add(moTargetField);
        panel.add(new JLabel("Ферромолибден"));

        // Алюминий
        panel.add(new JLabel("Алюминий (Al):"));
        panel.add(new JLabel("0.0")); // Начальный всегда 0
        alTargetField = new JTextField("0.05");
        panel.add(alTargetField);
        panel.add(new JLabel("Алюминий первичный"));

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
     * Загружает список марок стали из базы данных в выпадающий список.
     * Первым элементом добавляется пункт "-- Выберите марку --" для указания выбора.
     */
    private void loadSteelGrades() {
        List<SteelGrade> grades = DatabaseService.getAllSteelGrades();
        steelGradeCombo.removeAllItems();
        steelGradeCombo.addItem("-- Выберите марку --");

        for (SteelGrade grade : grades) {
            steelGradeCombo.addItem(grade.getName());
        }
    }

    /**
     * Загружает химический состав выбранной марки стали в поля целевых значений.
     * Автоматически заполняет поля целевого содержания элементов при выборе марки стали.
     */
    private void loadSteelGradeData() {
        String selectedGrade = (String) steelGradeCombo.getSelectedItem();
        if (selectedGrade != null && !selectedGrade.equals("-- Выберите марку --")) {
            SteelGrade grade = DatabaseService.findSteelGradeByName(selectedGrade);
            if (grade != null) {
                carbonTargetField.setText(formatWithDot(grade.getCarbon()));
                mnTargetField.setText(formatWithDot(grade.getManganese()));
                siTargetField.setText(formatWithDot(grade.getSilicon()));
                crTargetField.setText(formatWithDot(grade.getChromium()));
                niTargetField.setText(formatWithDot(grade.getNickel()));
                moTargetField.setText(formatWithDot(grade.getMolybdenum()));
                alTargetField.setText(formatWithDot(grade.getAluminum()));

                updateInitialValuesForGrade(selectedGrade);
            }
        }
    }

    /**
     * Форматирует числовое значение для отображения с точкой в качестве разделителя.
     *
     * @param value числовое значение для форматирования
     * @return строковое представление числа с точкой в качестве десятичного разделителя
     */
    private String formatWithDot(double value) {
        return String.format("%.2f", value).replace(',', '.');
    }

    /**
     * Обновляет начальные значения содержания элементов на основе выбранной марки стали.
     * Для элементов с нулевым содержанием в марке стали устанавливает начальное значение 0.0.
     *
     * @param steelGrade выбранная марка стали
     */
    private void updateInitialValuesForGrade(String steelGrade) {
        SteelGrade grade = DatabaseService.findSteelGradeByName(steelGrade);
        if (grade != null) {
            if (grade.getChromium() == 0.0) {
                crInitField.setText("0.0");
            }
            if (grade.getNickel() == 0.0) {
                niInitField.setText("0.0");
            }
            if (grade.getMolybdenum() == 0.0) {
                moInitField.setText("0.0");
            }
        }
    }

    /**
     * Выполняет расчет раскисления и легирования стали на основе введенных данных.
     * Включает валидацию входных данных, выполнение расчета и отображение результатов.
     *
     * <p><b>Процесс расчета:</b>
     * <ol>
     * <li>Проверка выбора марки стали</li>
     * <li>Валидация массы стали</li>
     * <li>Парсинг химических составов</li>
     * <li>Проверка корректности составов</li>
     * <li>Выполнение расчета через CalculatorService</li>
     * <li>Отображение результатов</li>
     * </ol>
     *
     * @throws NumberFormatException если введены некорректные числовые значения
     * @throws IllegalArgumentException если данные не проходят валидацию
     */
    private void calculate() {
        try {
            String steelGrade = (String) steelGradeCombo.getSelectedItem();
            if (steelGrade == null || steelGrade.equals("-- Выберите марку --")) {
                JOptionPane.showMessageDialog(this,
                        "Выберите марку стали из списка!",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double weight = parseDoubleField(weightField, "Масса стали");
            if (weight <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Масса стали должна быть положительным числом!",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Map<String, Double> initialComp = new HashMap<>();
            Map<String, Double> targetComp = new HashMap<>();

            initialComp.put("C", parseDoubleField(carbonInitField, "Углерод начальный"));
            initialComp.put("Mn", parseDoubleField(mnInitField, "Марганец начальный"));
            initialComp.put("Si", parseDoubleField(siInitField, "Кремний начальный"));
            initialComp.put("Cr", parseDoubleField(crInitField, "Хром начальный"));
            initialComp.put("Ni", parseDoubleField(niInitField, "Никель начальный"));
            initialComp.put("Mo", parseDoubleField(moInitField, "Молибден начальный"));
            initialComp.put("Al", 0.0);

            targetComp.put("C", parseDoubleField(carbonTargetField, "Углерод целевой"));
            targetComp.put("Mn", parseDoubleField(mnTargetField, "Марганец целевой"));
            targetComp.put("Si", parseDoubleField(siTargetField, "Кремний целевой"));
            targetComp.put("Cr", parseDoubleField(crTargetField, "Хром целевой"));
            targetComp.put("Ni", parseDoubleField(niTargetField, "Никель целевой"));
            targetComp.put("Mo", parseDoubleField(moTargetField, "Молибден целевой"));
            targetComp.put("Al", parseDoubleField(alTargetField, "Алюминий целевой"));

            if (!validateCompositions(initialComp, targetComp)) {
                return;
            }

            lastResult = calculator.calculateAlloying(weight, initialComp, targetComp, steelGrade);
            displayResults(lastResult);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Проверьте правильность введенных числовых значений!\n" +
                            "Используйте точку как разделитель дробной части.\n" +
                            "Пример: 0.25, 1.58, 100000\n\n" +
                            "Ошибка: " + e.getMessage(),
                    "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Ошибка при расчете: " + e.getMessage(),
                    "Ошибка расчета", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Парсит числовое значение из текстового поля с обработкой ошибок.
     *
     * @param field текстовое поле для парсинга
     * @param fieldName название поля для сообщений об ошибках
     * @return числовое значение из поля
     * @throws NumberFormatException если поле пустое или содержит некорректное значение
     */
    private double parseDoubleField(JTextField field, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new NumberFormatException("Поле '" + fieldName + "' не может быть пустым");
        }
        text = text.replace(',', '.');
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Неверный формат числа в поле '" + fieldName + "': " + text);
        }
    }

    /**
     * Проверяет корректность химических составов перед расчетом.
     *
     * @param initial начальный химический состав
     * @param target целевой химический состав
     * @return true если составы корректны, false в противном случае
     */
    private boolean validateCompositions(Map<String, Double> initial, Map<String, Double> target) {
        for (String element : target.keySet()) {
            double initialVal = initial.getOrDefault(element, 0.0);
            double targetVal = target.get(element);

            if (targetVal < initialVal) {
                JOptionPane.showMessageDialog(this,
                        "Целевое содержание " + element + " (" + targetVal + "%) " +
                                "не может быть меньше начального (" + initialVal + "%)!",
                        "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (targetVal < 0 || initialVal < 0) {
                JOptionPane.showMessageDialog(this,
                        "Содержание элементов не может быть отрицательным!",
                        "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /**
     * Отображает результаты расчета в текстовой области.
     * Форматирует вывод с разделами: исходные данные, необходимые материалы, итоговый состав.
     *
     * @param result объект с результатами расчета для отображения
     */
    private void displayResults(AlloyingResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("РЕЗУЛЬТАТЫ РАСЧЕТА РАСКИСЛЕНИЯ И ЛЕГИРОВАНИЯ\n");
        sb.append("=============================================\n\n");

        sb.append("ИСХОДНЫЕ ДАННЫЕ:\n");
        sb.append(String.format("Марка стали: %s\n", result.getSteelGrade()));
        sb.append(String.format("Масса плавки: %.0f кг\n", result.getInitialWeight()));

        sb.append("\nНачальный состав (%):\n");
        for (Map.Entry<String, Double> entry : result.getInitialComposition().entrySet()) {
            sb.append(String.format("  %s: %.3f%%\n", entry.getKey(), entry.getValue()));
        }

        sb.append("\nЦелевой состав (%):\n");
        for (Map.Entry<String, Double> entry : result.getTargetComposition().entrySet()) {
            sb.append(String.format("  %s: %.3f%%\n", entry.getKey(), entry.getValue()));
        }

        sb.append("\nНЕОБХОДИМЫЕ МАТЕРИАЛЫ:\n");
        sb.append("---------------------\n");
        double totalMaterials = 0;
        for (Map.Entry<String, Double> entry : result.getAddedMaterials().entrySet()) {
            sb.append(String.format("  %s: %.2f кг\n", entry.getKey(), entry.getValue()));
            totalMaterials += entry.getValue();
        }

        if (result.getCarbonAdditive() > 0) {
            sb.append(String.format("  Науглероживатель: %.2f кг\n", result.getCarbonAdditive()));
            totalMaterials += result.getCarbonAdditive();
        }

        sb.append(String.format("\nОбщая масса добавок: %.2f кг\n", totalMaterials));

        sb.append("\nРАСЧЕТНЫЙ ИТОГОВЫЙ СОСТАВ:\n");
        sb.append("-------------------------\n");
        for (Map.Entry<String, Double> entry : result.getFinalComposition().entrySet()) {
            sb.append(String.format("  %s: %.3f%%\n", entry.getKey(), entry.getValue()));
        }

        resultArea.setText(sb.toString());
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

        boolean success = DatabaseService.saveAlloyingResult(lastResult, currentUser);
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

        // Теперь используем реальные данные
        PDFExportService.exportAlloyingToPDF(lastResult, currentUser);
    }

    /**
     * Очищает поля ввода и результаты предыдущего расчета.
     * Сбрасывает форму к начальному состоянию с сохранением значений по умолчанию.
     */
    private void clearResults() {
        resultArea.setText("");
        weightField.setText("100000");
        carbonInitField.setText("0.15");
        mnInitField.setText("0.10");
        siInitField.setText("0.0");
        crInitField.setText("0.05");
        niInitField.setText("0.10");
        moInitField.setText("0.05");
        steelGradeCombo.setSelectedIndex(0);
    }

    /**
     * Отображает диалоговое окно с математическими формулами расчета раскисления.
     * Содержит основную формулу 7.1, коэффициенты угара и состав материалов.
     */
    private void showFormulas() {
        String formulas = """
            ФОРМУЛЫ РАСЧЕТА РАСКИСЛЕНИЯ И ЛЕГИРОВАНИЯ
            ========================================
            
            ОСНОВНАЯ ФОРМУЛА (7.1):
            M = [M_ж.ст. * (C_гот.ст. - C_исх) * 100] / [C_фер * (100 - K_уг)]
            
            где:
            M - масса ферросплава, кг
            M_ж.ст. - масса жидкой стали, кг
            C_гот.ст. - содержание элемента в готовой стали, %
            C_исх - содержание элемента перед раскислением, %
            C_фер - содержание элемента в ферросплаве, %
            K_уг - угар элемента, %
            
            КОЭФФИЦИЕНТЫ УГАРА ЭЛЕМЕНТОВ:
            • Алюминий (Al): 40%
            • Углерод (C): 40%
            • Кремний (Si): 15%
            • Марганец (Mn): 10%
            • Хром (Cr): 10%
            • Железо (Fe): 10%
            • Никель (Ni): 0%
            • Молибден (Mo): 0%
            
            СОСТАВ МАТЕРИАЛОВ:
            • Ферромарганец ФМн78: Mn=80%, C=7%, Si=1.5%
            • Ферросилиций ФС65: Si=65%, Mn=0.4%, Al=2.5%
            • Феррохром ФХ100А: Cr=68%, C=0.9%, Si=0.8%
            • Алюминий первичный: Al=97%, Si=1.5%
            • Науглероживатель: C=99.8%
            """;

        JTextArea textArea = new JTextArea(formulas, 25, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(this, scrollPane, "Формулы расчета раскисления", JOptionPane.INFORMATION_MESSAGE);
    }
}