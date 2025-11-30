package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Панель для визуализации алгоритмов расчетов в приложении "Калькулятор металлурга".
 * Предоставляет графическое представление алгоритмов раскисления стали и расчета параметров МНЛЗ
 * в виде блок-схем с подробными описаниями формул и процессов.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Визуализация алгоритма раскисления и легирования стали по разделу 7 учебного пособия</li>
 * <li>Визуализация алгоритма расчета параметров машины непрерывного литья заготовок (МНЛЗ) по разделу 8</li>
 * <li>Интерактивное переключение между алгоритмами через вкладки</li>
 * <li>Детальное описание математических формул и коэффициентов</li>
 * <li>Графическое представление логических ветвлений и циклов алгоритмов</li>
 * </ul>
 *
 * <p><b>Структура визуализации:</b>
 * <ul>
 * <li>Левая панель - текстовое описание алгоритма с формулами</li>
 * <li>Правая панель - графическая блок-схема алгоритма</li>
 * <li>Разделитель для регулировки соотношения панелей</li>
 * </ul>
 *
 * @author Саитова София
 * @version 1.0
 * @see JPanel
 * @see AlloyingFlowchartPanel
 * @see CasterFlowchartPanel
 * @since 2025
 */
public class AlgorithmVisualizationPanel extends JPanel {
    /**
     * Контейнер с вкладками для переключения между алгоритмами.
     * Содержит две вкладки: алгоритм раскисления и алгоритм расчета МНЛЗ.
     */
    private JTabbedPane tabbedPane;

    /**
     * Конструктор по умолчанию.
     * Инициализирует пользовательский интерфейс панели визуализации алгоритмов.
     */
    public AlgorithmVisualizationPanel() {
        initializeUI();
    }

    /**
     * Инициализирует пользовательский интерфейс панели.
     * Создает вкладки с алгоритмами и настраивает компоновку компонентов.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabbedPane = new JTabbedPane();

        // Панель для алгоритма раскисления
        JPanel alloyingPanel = createAlloyingAlgorithmPanel();
        tabbedPane.addTab("Алгоритм раскисления и легирования", alloyingPanel);

        // Панель для алгоритма МНЛЗ
        JPanel casterPanel = createCasterAlgorithmPanel();
        tabbedPane.addTab("Алгоритм расчета МНЛЗ", casterPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Создает панель для визуализации алгоритма раскисления и легирования стали.
     *
     * @return JPanel с разделенными областями описания и блок-схемы алгоритма
     */
    private JPanel createAlloyingAlgorithmPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Описание алгоритма
        JTextArea description = new JTextArea();
        description.setText("АЛГОРИТМ РАСЧЕТА РАСКИСЛЕНИЯ И ЛЕГИРОВАНИЯ СТАЛИ\n" +
                "==================================================\n\n" +
                "Основная формула (7.1):\n" +
                "M = [M_ж.ст. × (C_гот.ст. - C_исх) × 100] / [C_фер × (100 - K_уг)]\n\n" +
                "где:\n" +
                "M - масса ферросплава, кг\n" +
                "M_ж.ст. - масса жидкой стали, кг\n" +
                "C_гот.ст. - содержание элемента в готовой стали, %\n" +
                "C_исх - содержание элемента перед раскислением, %\n" +
                "C_фер - содержание элемента в ферросплаве, %\n" +
                "K_уг - угар элемента, %\n\n" +
                "Коэффициенты угара:\n" +
                "• Al, C: 40%    • Si: 15%    • Mn, Cr, Fe: 10%    • Ni, Mo: 0%");
        description.setEditable(false);
        description.setFont(new Font("Monospaced", Font.PLAIN, 12));
        description.setBackground(new Color(240, 240, 240));

        // Панель с блок-схемой
        JPanel flowchartPanel = new AlloyingFlowchartPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(description), new JScrollPane(flowchartPanel));
        splitPane.setDividerLocation(400);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Создает панель для визуализации алгоритма расчета параметров МНЛЗ.
     *
     * @return JPanel с разделенными областями описания и блок-схемы алгоритма
     */
    private JPanel createCasterAlgorithmPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Описание алгоритма
        JTextArea description = new JTextArea();
        description.setText("АЛГОРИТМ РАСЧЕТА ПАРАМЕТРОВ МНЛЗ\n" +
                "===================================\n\n" +
                "Основные формулы:\n\n" +
                "1. Число ручьев (8.2):\n" +
                "   n = M / (B × b × ρ × v × τ_max)\n\n" +
                "2. Металлургическая длина (8.4, 8.5):\n" +
                "   L_л = k_з × b² × v\n" +
                "   L_л = (k_з × b × M) / (0.9 × n × B × τ_р × ρ)\n\n" +
                "3. Радиус МНЛЗ (8.7):\n" +
                "   R = 2 × L_л / π\n\n" +
                "4. Минимальный радиус (8.9):\n" +
                "   R_min ≥ 42 × b\n\n" +
                "где:\n" +
                "M - масса плавки, кг\n" +
                "B, b - ширина и толщина заготовки, м\n" +
                "ρ = 7300 кг/м³ - плотность стали\n" +
                "v - скорость разливки, м/мин\n" +
                "τ - время разливки, мин\n" +
                "k_з - коэффициент затвердевания");
        description.setEditable(false);
        description.setFont(new Font("Monospaced", Font.PLAIN, 12));
        description.setBackground(new Color(240, 240, 240));

        // Панель с блок-схемой
        JPanel flowchartPanel = new CasterFlowchartPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(description), new JScrollPane(flowchartPanel));
        splitPane.setDividerLocation(400);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }
}

/**
 * Панель для отрисовки блок-схемы алгоритма раскисления и легирования стали.
 * Визуализирует последовательность шагов расчета согласно разделу 7 учебного пособия.
 *
 * <p><b>Элементы блок-схемы:</b>
 * <ul>
 * <li>Терминаторы (эллипсы) - начало и конец алгоритма</li>
 * <li>Процессы (прямоугольники) - вычислительные операции</li>
 * <li>Решение (ромбы) - логические ветвления</li>
 * <li>Ввод/вывод (параллелограммы) - операции ввода и вывода данных</li>
 * <li>Стрелки - направление выполнения алгоритма</li>
 * </ul>
 *
 * <p><b>Основные этапы алгоритма:</b>
 * <ol>
 * <li>Ввод исходных данных</li>
 * <li>Проверка корректности данных</li>
 * <li>Циклический расчет для каждого химического элемента</li>
 * <li>Расчет масс ферросплавов при необходимости</li>
 * <li>Учет вносимых примесей</li>
 * <li>Корректировка углерода</li>
 * <li>Вывод результатов</li>
 * </ol>
 */
class AlloyingFlowchartPanel extends JPanel {
    /**
     * Ширина блока элементов блок-схемы в пикселях.
     */
    private static final int BLOCK_WIDTH = 280;

    /**
     * Высота блока элементов блок-схемы в пикселях.
     */
    private static final int BLOCK_HEIGHT = 50;

    /**
     * Вертикальное расстояние между блоками в пикселях.
     */
    private static final int VERTICAL_SPACING = 80;

    /**
     * Горизонтальное расстояние между параллельными ветвями в пикселях.
     */
    private static final int HORIZONTAL_SPACING = 80;

    /**
     * Конструктор панели блок-схемы раскисления.
     * Устанавливает предпочтительный размер для корректного отображения схемы.
     */
    public AlloyingFlowchartPanel() {
        setPreferredSize(new Dimension(1000, 1000));
        setBackground(Color.WHITE);
    }

    /**
     * Отрисовывает блок-схему алгоритма раскисления стали.
     *
     * @param g объект Graphics для отрисовки
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2 - BLOCK_WIDTH / 2;
        int currentY = 50;

        // 1. Начало (Эллипс)
        drawTerminator(g2d, centerX, currentY, "Начало расчета");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 2. Ввод данных (Параллелограмм)
        drawInputOutput(g2d, centerX, currentY, "Ввод: M, [C]исх, [C]цел, марка стали");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 3. Проверка данных (РОМБ 1)
        int decisionY = currentY;
        drawDecision(g2d, centerX, currentY, "Данные корректны?");

        int decisionCenterX = centerX + BLOCK_WIDTH/2;
        int diamondBottomY = decisionY + BLOCK_HEIGHT;

        // ВЕТКА "НЕТ" - влево (завершение программы)
        int noEndX = centerX - HORIZONTAL_SPACING - BLOCK_WIDTH;
        drawHorizontalArrow(g2d, centerX, decisionY + BLOCK_HEIGHT/2, noEndX, decisionY + BLOCK_HEIGHT/2);
        drawProcess(g2d, noEndX, decisionY + BLOCK_HEIGHT/2 - BLOCK_HEIGHT/2, "Завершение\nпрограммы");

        // Стрелка из блока завершения в конечный терминатор
        drawVerticalArrow(g2d, noEndX + BLOCK_WIDTH/2, decisionY + BLOCK_HEIGHT, decisionY + BLOCK_HEIGHT + VERTICAL_SPACING);
        drawTerminator(g2d, noEndX, decisionY + BLOCK_HEIGHT + VERTICAL_SPACING, "Конец расчета");

        // ВЕТКА "ДА" - вниз (продолжение расчета)
        currentY = diamondBottomY + VERTICAL_SPACING;
        drawVerticalArrow(g2d, decisionCenterX, diamondBottomY, currentY);

        // 4. Инициализация
        drawProcess(g2d, centerX, currentY, "Инициализация: finalComp = initialComp");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // ========== ЦИКЛ ПО ЭЛЕМЕНТАМ ==========
        int loopStartY = currentY;

        // 5. Начало цикла
        drawProcess(g2d, centerX, currentY, "Для каждого элемента: Mn, Si, Cr, Al...");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 6. Расчет прироста
        drawProcess(g2d, centerX, currentY, "ΔC = C_цел - C_исх");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 7. Проверка прироста (РОМБ 2)
        int deltaDecisionY = currentY;
        drawDecision(g2d, centerX, currentY, "ΔC > 0?");
        int deltaDiamondBottomY = deltaDecisionY + BLOCK_HEIGHT;

        // ВЕТКА "ДА" - вправо (расчет ферросплава)
        int yesX = centerX + BLOCK_WIDTH + HORIZONTAL_SPACING;
        drawHorizontalArrow(g2d, centerX + BLOCK_WIDTH, deltaDecisionY + BLOCK_HEIGHT/2, yesX, deltaDecisionY + BLOCK_HEIGHT/2);
        drawProcess(g2d, yesX, deltaDecisionY + BLOCK_HEIGHT/2 - BLOCK_HEIGHT/2, "Расчет массы\nферросплава");

        // ВЕТКА "НЕТ" - влево (пропуск расчета)
        int noX = centerX - HORIZONTAL_SPACING - BLOCK_WIDTH;
        drawHorizontalArrow(g2d, centerX, deltaDecisionY + BLOCK_HEIGHT/2, noX, deltaDecisionY + BLOCK_HEIGHT/2);
        drawProcess(g2d, noX, deltaDecisionY + BLOCK_HEIGHT/2 - BLOCK_HEIGHT/2, "Пропуск\nрасчета");

        // Обе ветки соединяются и идут вниз к блоку "Учет примесей" - ИСПОЛЬЗУЕМ НОВЫЙ МЕТОД
        int impuritiesY = drawCycleArrows(g2d, centerX, decisionCenterX, deltaDecisionY, deltaDiamondBottomY, yesX, noX);

        // Блок "Учет примесей"
        currentY = impuritiesY;
        drawProcess(g2d, centerX, currentY, "Учет вносимых\nпримесей");
        int impuritiesBottomY = currentY + BLOCK_HEIGHT;

        // Стрелка обратно в цикл (к следующему элементу)
        int loopReturnY = impuritiesBottomY + 20;
        drawVerticalArrow(g2d, decisionCenterX, impuritiesBottomY, loopReturnY + 10);


        // Подписи ДА/НЕТ для ромбов
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        // Первый ромб - ДА вниз (правее стрелки)
        g2d.drawString("ДА", decisionCenterX + 8, diamondBottomY + 15);
        g2d.drawString("НЕТ", centerX - 25, decisionY + BLOCK_HEIGHT/2 - 5);
        // Второй ромб
        g2d.drawString("ДА", centerX + BLOCK_WIDTH + 5, deltaDecisionY + BLOCK_HEIGHT/2 - 5);
        g2d.drawString("НЕТ", centerX - 25, deltaDecisionY + BLOCK_HEIGHT/2 - 5);
        g2d.setColor(Color.BLACK);

        // ========== ВЫХОД ИЗ ЦИКЛА И ПРОДОЛЖЕНИЕ АЛГОРИТМА ==========

        // 8. Корректировка углерода
        currentY = loopReturnY + 10;
        drawProcess(g2d, centerX , currentY, "Корректировка углерода\n(науглероживание)");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 9. Вывод результатов
        drawInputOutput(g2d, centerX, currentY, "Вывод: массы материалов,\nитоговый состав");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 10. Конец расчета (основной)
        drawTerminator(g2d, centerX, currentY, "Конец расчета");
    }

    /**
     * Рисует соединительные стрелки для циклической части алгоритма.
     * Обеспечивает правильное соединение ветвей "ДА" и "НЕТ" из блока проверки прироста элемента.
     *
     * @param g2d объект Graphics2D для отрисовки
     * @param centerX центральная координата X основного потока
     * @param decisionCenterX центральная координата X блока принятия решений
     * @param deltaDecisionY координата Y блока проверки прироста
     * @param deltaDiamondBottomY нижняя координата Y ромба проверки прироста
     * @param yesX координата X ветки "ДА"
     * @param noX координата X ветки "НЕТ"
     * @return координата Y для размещения следующего блока алгоритма
     */
    private int drawCycleArrows(Graphics2D g2d, int centerX, int decisionCenterX,
                                int deltaDecisionY, int deltaDiamondBottomY, int yesX, int noX) {
        int arrowSize = 8;

        // Обе ветки соединяются и идут вниз к блоку "Учет примесей"
        int mergeY = deltaDiamondBottomY + 40;

        // Стрелка от "ДА" (расчет ферросплава) - вниз до точки слияния
        int yesBottomY = deltaDecisionY + BLOCK_HEIGHT/2 + BLOCK_HEIGHT/2;
        g2d.setColor(Color.BLACK);
        g2d.drawLine(yesX + BLOCK_WIDTH/2, yesBottomY, yesX + BLOCK_WIDTH/2, mergeY);

        // Стрелка от "НЕТ" (пропуск расчета) - вниз до точки слияния
        g2d.drawLine(noX + BLOCK_WIDTH/2, yesBottomY, noX + BLOCK_WIDTH/2, mergeY);

        // Горизонтальные стрелки от обеих веток к центру
        g2d.drawLine(yesX + BLOCK_WIDTH/2, mergeY, decisionCenterX, mergeY);
        g2d.drawLine(noX + BLOCK_WIDTH/2, mergeY, decisionCenterX, mergeY);

        // Стрелка вниз от точки слияния к блоку "Учет примесей" - ДОХОДИТ ДО САМОГО БЛОКА
        int impuritiesY = mergeY + 40;
        g2d.drawLine(decisionCenterX, mergeY, decisionCenterX, impuritiesY);

        // Добавляем наконечник стрелки прямо перед блоком "Учет примесей"
        g2d.fillPolygon(new int[]{decisionCenterX, decisionCenterX - arrowSize, decisionCenterX + arrowSize},
                new int[]{impuritiesY, impuritiesY - arrowSize, impuritiesY - arrowSize}, 3);

        return impuritiesY;
    }

    /**
     * Рисует терминатор (начало/конец алгоритма) в виде эллипса.
     *
     * @param g2d объект Graphics2D для отрисовки
     * @param x координата X левого верхнего угла
     * @param y координата Y левого верхнего угла
     * @param text текст для отображения внутри терминатора
     */
    private void drawTerminator(Graphics2D g2d, int x, int y, String text) {
        Ellipse2D ellipse = new Ellipse2D.Double(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
        g2d.setColor(new Color(200, 255, 200));
        g2d.fill(ellipse);
        g2d.setColor(Color.BLACK);
        g2d.draw(ellipse);
        drawCenteredString(g2d, text, x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
    }

    /**
     * Рисует блок процесса (вычислительная операция) в виде прямоугольника.
     *
     * @param g2d объект Graphics2D для отрисовки
     * @param x координата X левого верхнего угла
     * @param y координата Y левого верхнего угла
     * @param text текст для отображения внутри блока
     */
    private void drawProcess(Graphics2D g2d, int x, int y, String text) {
        Rectangle rect = new Rectangle(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
        g2d.setColor(new Color(200, 230, 255));
        g2d.fill(rect);
        g2d.setColor(Color.BLACK);
        g2d.draw(rect);
        drawCenteredString(g2d, text, x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
    }

    /**
     * Рисует блок принятия решения (логическое ветвление) в виде ромба.
     *
     * @param g2d объект Graphics2D для отрисовки
     * @param x координата X левого верхнего угла
     * @param y координата Y левого верхнего угла
     * @param text текст для отображения внутри ромба
     */
    private void drawDecision(Graphics2D g2d, int x, int y, String text) {
        int centerX = x + BLOCK_WIDTH/2;
        int centerY = y + BLOCK_HEIGHT/2;

        Polygon diamond = new Polygon();
        diamond.addPoint(centerX, y);
        diamond.addPoint(x + BLOCK_WIDTH, centerY);
        diamond.addPoint(centerX, y + BLOCK_HEIGHT);
        diamond.addPoint(x, centerY);

        g2d.setColor(new Color(255, 255, 200));
        g2d.fill(diamond);
        g2d.setColor(Color.BLACK);
        g2d.draw(diamond);
        drawCenteredString(g2d, text, x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
    }

    /**
     * Рисует блок ввода/вывода данных в виде параллелограмма.
     *
     * @param g2d объект Graphics2D для отрисовки
     * @param x координата X левого верхнего угла
     * @param y координата Y левого верхнего угла
     * @param text текст для отображения внутри блока
     */
    private void drawInputOutput(Graphics2D g2d, int x, int y, String text) {
        int offset = 20;
        Polygon parallelogram = new Polygon();
        parallelogram.addPoint(x + offset, y);
        parallelogram.addPoint(x + BLOCK_WIDTH, y);
        parallelogram.addPoint(x + BLOCK_WIDTH - offset, y + BLOCK_HEIGHT);
        parallelogram.addPoint(x, y + BLOCK_HEIGHT);

        g2d.setColor(new Color(255, 200, 255));
        g2d.fill(parallelogram);
        g2d.setColor(Color.BLACK);
        g2d.draw(parallelogram);
        drawCenteredString(g2d, text, x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
    }

    /**
     * Рисует вертикальную стрелку между блоками.
     *
     * @param g2d объект Graphics2D для отрисовки
     * @param x координата X центра стрелки
     * @param fromY координата Y начала стрелки
     * @param toY координата Y конца стрелки
     */
    private void drawVerticalArrow(Graphics2D g2d, int x, int fromY, int toY) {
        g2d.setColor(Color.BLACK);
        g2d.drawLine(x, fromY, x, toY);

        int arrowSize = 8;
        if (toY > fromY) {
            g2d.fillPolygon(new int[]{x, x - arrowSize, x + arrowSize},
                    new int[]{toY, toY - arrowSize, toY - arrowSize}, 3);
        }
    }

    /**
     * Рисует горизонтальную стрелку между блоками.
     *
     * @param g2d объект Graphics2D для отрисовки
     * @param fromX координата X начала стрелки
     * @param y координата Y начала стрелки
     * @param toX координата X конца стрелки
     * @param toY координата Y конца стрелки
     */
    private void drawHorizontalArrow(Graphics2D g2d, int fromX, int y, int toX, int toY) {
        g2d.setColor(Color.BLACK);
        g2d.drawLine(fromX, y, toX, toY);

        int arrowSize = 8;
        if (toX > fromX) {
            g2d.fillPolygon(new int[]{toX, toX - arrowSize, toX - arrowSize},
                    new int[]{toY, toY - arrowSize, toY + arrowSize}, 3);
        } else {
            g2d.fillPolygon(new int[]{toX, toX + arrowSize, toX + arrowSize},
                    new int[]{toY, toY - arrowSize, toY + arrowSize}, 3);
        }
    }

    /**
     * Рисует центрированный текст внутри блока блок-схемы.
     * Поддерживает многострочный текст с автоматическим переносом строк.
     *
     * @param g2d объект Graphics2D для отрисовки
     * @param text текст для отображения
     * @param x координата X блока
     * @param y координата Y блока
     * @param width ширина блока
     * @param height высота блока
     */
    private void drawCenteredString(Graphics2D g2d, String text, int x, int y, int width, int height) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            int textX = x + (width - fm.stringWidth(lines[i])) / 2;
            int textY = y + (height - fm.getHeight() * lines.length) / 2 + fm.getAscent() + i * fm.getHeight();
            g2d.drawString(lines[i], textX, textY);
        }
    }
}

/**
 * Панель для отрисовки блок-схемы алгоритма расчета параметров машины непрерывного литья заготовок (МНЛЗ).
 * Визуализирует последовательность шагов расчета согласно разделу 8 учебного пособия.
 *
 * <p><b>Основные этапы алгоритма:</b>
 * <ol>
 * <li>Ввод исходных параметров разливки</li>
 * <li>Расчет числа ручьев МНЛЗ</li>
 * <li>Определение коэффициента затвердевания по типу заготовки</li>
 * <li>Расчет металлургической длины</li>
 * <li>Расчет радиуса МНЛЗ</li>
 * <li>Проверка минимального радиуса</li>
 * <li>Определение высоты машины</li>
 * <li>Вывод расчетных параметров</li>
 * </ol>
 */
class CasterFlowchartPanel extends JPanel {
    /**
     * Ширина блока элементов блок-схемы в пикселях.
     */
    private static final int BLOCK_WIDTH = 300;

    /**
     * Высота блока элементов блок-схемы в пикселях.
     */
    private static final int BLOCK_HEIGHT = 50;

    /**
     * Вертикальное расстояние между блоками в пикселях.
     */
    private static final int VERTICAL_SPACING = 80;

    /**
     * Горизонтальное расстояние между параллельными ветвями в пикселях.
     */
    private static final int HORIZONTAL_SPACING = 80;

    /**
     * Конструктор панели блок-схемы расчета МНЛЗ.
     * Устанавливает предпочтительный размер для корректного отображения схемы.
     */
    public CasterFlowchartPanel() {
        setPreferredSize(new Dimension(1000, 800));
        setBackground(Color.WHITE);
    }

    /**
     * Отрисовывает блок-схему алгоритма расчета параметров МНЛЗ.
     *
     * @param g объект Graphics для отрисовки
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2 - BLOCK_WIDTH / 2;
        int currentY = 50;

        // 1. Начало
        drawTerminator(g2d, centerX, currentY, "Начало расчета МНЛЗ");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 2. Ввод данных
        drawInputOutput(g2d, centerX, currentY, "Ввод: M, B, b, v, марка стали, τ");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 3. Расчет числа ручьев
        drawProcess(g2d, centerX, currentY, "Расчет числа ручьев\nn = M / (B × b × ρ × v × τ_max)");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 4. Определение k_з
        drawProcess(g2d, centerX, currentY, "Определение коэффициента k_з\nпо типу заготовки");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 5. Расчет металлургической длины
        drawProcess(g2d, centerX, currentY, "Расчет L_л = (k_з × b × M) / \n(0.9 × n × B × τ_р × ρ)");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 6. Расчет радиуса
        drawProcess(g2d, centerX, currentY, "Расчет R = 2 × L_л / π");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 7. Проверка минимального радиуса (РОМБ)
        int radiusDecisionY = currentY;
        drawDecision(g2d, centerX, currentY, "R ≥ R_min = 42 × b?");
        int radiusDiamondBottomY = radiusDecisionY + BLOCK_HEIGHT;
        int decisionCenterX = centerX + BLOCK_WIDTH/2;

        // ВЕТКА "НЕТ" - влево (корректировка радиуса)
        int noX = centerX - HORIZONTAL_SPACING - BLOCK_WIDTH;
        drawHorizontalArrow(g2d, centerX, radiusDecisionY + BLOCK_HEIGHT/2, noX, radiusDecisionY + BLOCK_HEIGHT/2);
        drawProcess(g2d, noX, radiusDecisionY + BLOCK_HEIGHT/2 - BLOCK_HEIGHT/2, "R = R_min");

        // Стрелка из блока коррекции к блоку "H = R"
        int correctionCenterX = noX + BLOCK_WIDTH/2;
        int correctionBottomY = radiusDecisionY + BLOCK_HEIGHT/2 + BLOCK_HEIGHT/2;

        // Определяем координаты для L-образной стрелки
        int targetY = radiusDiamondBottomY + VERTICAL_SPACING; // Y блока "H = R"
        int turnY = correctionBottomY + 105; // Точка поворота (вниз)
        int hBlockLeftX = centerX; // Левая граница блока "H = R"

        // Вертикальная линия вниз от блока "R = R_min" (БЕЗ наконечника)
        g2d.setColor(Color.BLACK);
        g2d.drawLine(correctionCenterX, correctionBottomY, correctionCenterX, turnY);

        // Горизонтальная линия вправо до левого края блока "H = R" (С наконечником)
        g2d.drawLine(correctionCenterX, turnY, hBlockLeftX, turnY);

        // Добавляем наконечник стрелки на горизонтальной части (указывает вправо в блок "H = R")
        int arrowSize = 8;
        g2d.fillPolygon(new int[]{hBlockLeftX, hBlockLeftX - arrowSize, hBlockLeftX - arrowSize},
                new int[]{turnY, turnY - arrowSize, turnY + arrowSize}, 3);

        // ВЕТКА "ДА" - вниз (радиус корректен)
        currentY = radiusDiamondBottomY + VERTICAL_SPACING;
        drawVerticalArrow(g2d, decisionCenterX, radiusDiamondBottomY, currentY);

        // Блок "H = R"
        drawProcess(g2d, centerX, currentY, "H = R");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 8. Вывод результатов
        drawInputOutput(g2d, centerX, currentY, "Вывод: n, L_л, R, H");
        currentY += VERTICAL_SPACING;
        drawVerticalArrow(g2d, centerX + BLOCK_WIDTH/2, currentY - VERTICAL_SPACING + BLOCK_HEIGHT, currentY);

        // 9. Конец
        drawTerminator(g2d, centerX, currentY, "Конец расчета");

        // Подписи ДА/НЕТ
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        // ДА вниз для ромба проверки радиуса (правее стрелки)
        g2d.drawString("ДА", decisionCenterX + 8, radiusDiamondBottomY + 15);
        g2d.drawString("НЕТ", centerX - 25, radiusDecisionY + BLOCK_HEIGHT/2 - 5);
        g2d.setColor(Color.BLACK);
    }

    // Остальные методы аналогичны AlloyingFlowchartPanel...
    private void drawTerminator(Graphics2D g2d, int x, int y, String text) {
        Ellipse2D ellipse = new Ellipse2D.Double(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
        g2d.setColor(new Color(200, 255, 200));
        g2d.fill(ellipse);
        g2d.setColor(Color.BLACK);
        g2d.draw(ellipse);
        drawCenteredString(g2d, text, x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
    }

    private void drawProcess(Graphics2D g2d, int x, int y, String text) {
        Rectangle rect = new Rectangle(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
        g2d.setColor(new Color(200, 230, 255));
        g2d.fill(rect);
        g2d.setColor(Color.BLACK);
        g2d.draw(rect);
        drawCenteredString(g2d, text, x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
    }

    private void drawDecision(Graphics2D g2d, int x, int y, String text) {
        int centerX = x + BLOCK_WIDTH/2;
        int centerY = y + BLOCK_HEIGHT/2;

        Polygon diamond = new Polygon();
        diamond.addPoint(centerX, y);
        diamond.addPoint(x + BLOCK_WIDTH, centerY);
        diamond.addPoint(centerX, y + BLOCK_HEIGHT);
        diamond.addPoint(x, centerY);

        g2d.setColor(new Color(255, 255, 200));
        g2d.fill(diamond);
        g2d.setColor(Color.BLACK);
        g2d.draw(diamond);
        drawCenteredString(g2d, text, x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
    }

    private void drawInputOutput(Graphics2D g2d, int x, int y, String text) {
        int offset = 20;
        Polygon parallelogram = new Polygon();
        parallelogram.addPoint(x + offset, y);
        parallelogram.addPoint(x + BLOCK_WIDTH, y);
        parallelogram.addPoint(x + BLOCK_WIDTH - offset, y + BLOCK_HEIGHT);
        parallelogram.addPoint(x, y + BLOCK_HEIGHT);

        g2d.setColor(new Color(255, 200, 255));
        g2d.fill(parallelogram);
        g2d.setColor(Color.BLACK);
        g2d.draw(parallelogram);
        drawCenteredString(g2d, text, x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
    }

    private void drawVerticalArrow(Graphics2D g2d, int x, int fromY, int toY) {
        g2d.setColor(Color.BLACK);
        g2d.drawLine(x, fromY, x, toY);

        int arrowSize = 8;
        if (toY > fromY) {
            g2d.fillPolygon(new int[]{x, x - arrowSize, x + arrowSize},
                    new int[]{toY, toY - arrowSize, toY - arrowSize}, 3);
        } else {
            g2d.fillPolygon(new int[]{x, x - arrowSize, x + arrowSize},
                    new int[]{toY, toY + arrowSize, toY + arrowSize}, 3);
        }
    }

    private void drawHorizontalArrow(Graphics2D g2d, int fromX, int y, int toX, int toY) {
        g2d.setColor(Color.BLACK);
        g2d.drawLine(fromX, y, toX, toY);

        int arrowSize = 8;
        if (toX > fromX) {
            g2d.fillPolygon(new int[]{toX, toX - arrowSize, toX - arrowSize},
                    new int[]{toY, toY - arrowSize, toY + arrowSize}, 3);
        } else {
            g2d.fillPolygon(new int[]{toX, toX + arrowSize, toX + arrowSize},
                    new int[]{toY, toY - arrowSize, toY + arrowSize}, 3);
        }
    }

    private void drawCenteredString(Graphics2D g2d, String text, int x, int y, int width, int height) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            int textX = x + (width - fm.stringWidth(lines[i])) / 2;
            int textY = y + (height - fm.getHeight() * lines.length) / 2 + fm.getAscent() + i * fm.getHeight();
            g2d.drawString(lines[i], textX, textY);
        }
    }
}