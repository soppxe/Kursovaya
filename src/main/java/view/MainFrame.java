package view;

import javax.swing.*;

/**
 * Главное окно приложения "Калькулятор металлурга".
 * Предоставляет основной пользовательский интерфейс с вкладками для различных модулей расчета
 * и управления системой.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Организация модулей расчета через систему вкладок</li>
 * <li>Управление пользовательской сессией</li>
 * <li>Предоставление доступа к основным функциям приложения</li>
 * <li>Навигация между различными типами расчетов</li>
 * <li>Экспорт отчетов и управление историей расчетов</li>
 * </ul>
 *
 * <p><b>Структура интерфейса:</b>
 * <ul>
 * <li>Вкладка "Раскисление и легирование" - расчет ферросплавов для стали</li>
 * <li>Вкладка "Расчет МНЛЗ" - параметры машины непрерывного литья заготовок</li>
 * <li>Вкладка "Визуализация алгоритмов" - графическое представление расчетных методов</li>
 * <li>Вкладка "Отчеты и история" - управление сохраненными расчетами и экспорт</li>
 * </ul>
 *
 * <p><b>Меню приложения:</b>
 * <ul>
 * <li>Файл - операции экспорта и выхода из системы</li>
 * <li>Справка - информация о программе и разработчике</li>
 * </ul>
 *
 * @author Саитова София Александровна
 * @version 1.0
 * @see JFrame
 * @see AlloyingPanel
 * @see CasterPanel
 * @see AlgorithmVisualizationPanel
 * @see ReportsPanel
 * @since 2024
 */
public class MainFrame extends JFrame {
    /**
     * Имя текущего авторизованного пользователя.
     * Используется для персонализации интерфейса и идентификации при сохранении расчетов.
     */
    private String currentUser;

    /**
     * Конструктор главного окна приложения.
     * Инициализирует интерфейс с учетом имени пользователя для персонализации.
     *
     * @param username имя пользователя, вошедшего в систему
     * @throws IllegalArgumentException если username равен null или пустой строке
     */
    public MainFrame(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        this.currentUser = username;
        initializeUI();
    }

    /**
     * Инициализирует пользовательский интерфейс главного окна.
     * Создает и настраивает все компоненты: вкладки, меню, обработчики событий.
     *
     * <p><b>Настройки окна:</b>
     * <ul>
     * <li>Заголовок с именем пользователя</li>
     * <li>Размер 1000x700 пикселей</li>
     * <li>Центрирование на экране</li>
     * <li>Завершение приложения при закрытии</li>
     * </ul>
     *
     * <p><b>Создаваемые компоненты:</b>
     * <ul>
     * <li>Система вкладок с основными модулями</li>
     * <li>Главное меню с пунктами Файл и Справка</li>
     * <li>Обработчики событий для элементов меню</li>
     * </ul>
     */
    private void initializeUI() {
        setTitle("Калькулятор металлурга - " + currentUser);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Вкладка расчета раскисления и легирования
        AlloyingPanel alloyingPanel = new AlloyingPanel(currentUser);
        tabbedPane.addTab("Раскисление и легирование", alloyingPanel);

        // Вкладка расчета МНЛЗ
        CasterPanel casterPanel = new CasterPanel(currentUser);
        tabbedPane.addTab("Расчет МНЛЗ", casterPanel);

        // Вкладка визуализации алгоритмов
        AlgorithmVisualizationPanel algorithmPanel = new AlgorithmVisualizationPanel();
        tabbedPane.addTab("Визуализация алгоритмов", algorithmPanel);

        // Вкладка истории и отчетов
        ReportsPanel reportsPanel = new ReportsPanel(currentUser);
        tabbedPane.addTab("Отчеты и история", reportsPanel);

        add(tabbedPane);

        // Меню
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem exportItem = new JMenuItem("Экспорт отчетов");
        JMenuItem exitItem = new JMenuItem("Выход");

        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu("Справка");
        JMenuItem aboutItem = new JMenuItem("О программе");
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        // Обработчики меню
        exportItem.addActionListener(e -> showExportDialog());
        exitItem.addActionListener(e -> {
            new auth.LoginFrame().setVisible(true);
            dispose();
        });
        aboutItem.addActionListener(e -> showAboutDialog());
    }

    /**
     * Отображает диалоговое окно с информацией об экспорте отчетов.
     * Направляет пользователя на соответствующую вкладку для выполнения экспорта.
     */
    private void showExportDialog() {
        JOptionPane.showMessageDialog(this,
                "Для экспорта отчетов перейдите на вкладку 'Отчеты'\n" +
                        "и используйте кнопку 'Экспорт в PDF' для выбранного расчета.",
                "Экспорт отчетов", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Отображает диалоговое окно "О программе" с информацией о разработке.
     * Содержит сведения о версии, назначении программы и авторе.
     */
    private void showAboutDialog() {
        String aboutText =
                "Калькулятор металлурга\n" +
                        "Версия 1.0\n\n" +
                        "Программа для расчетов в металлургическом производстве:\n" +
                        "• Раскисление и легирование стали\n" +
                        "• Расчет параметров МНЛЗ\n\n" +
                        "Разработано для курсового проекта \n" +
                        "«Разработка информационной системы с использованием технологии ООП»\n " +
                        "Автор: Саитова София Александровна, группа ИСТ-23";

        JOptionPane.showMessageDialog(this, aboutText,
                "О программе", JOptionPane.INFORMATION_MESSAGE);
    }
}