package view;

import auth.LoginFrame;

import javax.swing.*;

public class MainFrame extends JFrame {
    private String currentUser;

    public MainFrame(String username) {
        this.currentUser = username;
        initializeUI();
    }

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

        // Вкладка истории и отчетов
        JPanel reportsPanel = new JPanel();
        reportsPanel.add(new JLabel("История расчетов и отчеты"));
        tabbedPane.addTab("Отчеты", reportsPanel);

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

    private void showExportDialog() {
        JOptionPane.showMessageDialog(this,
                "Для экспорта отчетов перейдите на вкладку 'Отчеты'\n" +
                        "и используйте кнопку 'Экспорт в PDF' для выбранного расчета.",
                "Экспорт отчетов", JOptionPane.INFORMATION_MESSAGE);
    }

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