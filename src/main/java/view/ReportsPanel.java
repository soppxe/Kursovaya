package view;

import model.AlloyingResult;
import model.CasterResult;
import service.DatabaseService;
import service.PDFExportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel {
    private String currentUser;
    private JTable historyTable;
    private DefaultTableModel tableModel;

    public ReportsPanel(String username) {
        this.currentUser = username;
        initializeUI();
        loadHistory();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Заголовок
        JLabel titleLabel = new JLabel("История расчетов и отчеты - " + currentUser, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Панель с таблицей истории
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Панель кнопок операций
        JPanel operationsPanel = createOperationsPanel();
        add(operationsPanel, BorderLayout.SOUTH);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Тип расчета", "Марка стали", "Основные параметры", "Дата"};
        tableModel = new DefaultTableModel(columns, 0);

        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton refreshBtn = new JButton("Обновить");
        JButton detailsBtn = new JButton("Показать детали");
        JButton exportPDFBtn = new JButton("Экспорт в PDF");
        JButton deleteBtn = new JButton("Удалить");
        JButton statsBtn = new JButton("Статистика");

        panel.add(refreshBtn);
        panel.add(detailsBtn);
        panel.add(exportPDFBtn);
        panel.add(deleteBtn);
        panel.add(statsBtn);

        refreshBtn.addActionListener(e -> loadHistory());
        detailsBtn.addActionListener(e -> showDetails());
        exportPDFBtn.addActionListener(e -> exportToPDF());
        deleteBtn.addActionListener(e -> deleteSelected());
        statsBtn.addActionListener(e -> showStatistics());

        return panel;
    }

    private void loadHistory() {
        tableModel.setRowCount(0);

        // Загружаем реальные данные из базы
        List<AlloyingResult> alloyingResults = DatabaseService.getAlloyingHistory(currentUser);
        List<CasterResult> casterResults = DatabaseService.getCasterHistory(currentUser);

        // Добавляем расчеты раскисления
        for (AlloyingResult result : alloyingResults) {
            tableModel.addRow(new Object[]{
                    result.getId(),
                    "Раскисление",
                    result.getSteelGrade(),
                    String.format("Масса: %.0f кг, Добавки: %.1f кг",
                            result.getInitialWeight(),
                            getTotalMaterials(result)),
                    "Расчет сохранен"
            });
        }

        // Добавляем расчеты МНЛЗ
        for (CasterResult result : casterResults) {
            tableModel.addRow(new Object[]{
                    result.getId(),
                    "МНЛЗ",
                    result.getSteelGrade(),
                    String.format("Ручьев: %d, Длина: %.1f м",
                            result.getNumberOfStreams(),
                            result.getMetallurgicalLength()),
                    "Расчет сохранен"
            });
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Нет сохраненных расчетов. Выполните расчеты и сохраните их.",
                    "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private double getTotalMaterials(AlloyingResult result) {
        double total = 0;
        for (Double value : result.getAddedMaterials().values()) {
            total += value;
        }
        return total + result.getCarbonAdditive();
    }

    private void showDetails() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0) {
            String reportType = (String) tableModel.getValueAt(selectedRow, 1);
            int resultId = (Integer) tableModel.getValueAt(selectedRow, 0);

            if ("Раскисление".equals(reportType)) {
                showAlloyingDetails(resultId);
            } else if ("МНЛЗ".equals(reportType)) {
                showCasterDetails(resultId);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите запись для просмотра деталей!");
        }
    }

    private void showAlloyingDetails(int resultId) {
        List<AlloyingResult> results = DatabaseService.getAlloyingHistory(currentUser);
        for (AlloyingResult result : results) {
            if (result.getId() == resultId) {
                StringBuilder details = new StringBuilder();
                details.append("Детали расчета раскисления:\n\n");
                details.append("Марка стали: ").append(result.getSteelGrade()).append("\n");
                details.append("Масса плавки: ").append(result.getInitialWeight()).append(" кг\n\n");

                details.append("Материалы:\n");
                for (var entry : result.getAddedMaterials().entrySet()) {
                    details.append(String.format("  %s: %.2f кг\n", entry.getKey(), entry.getValue()));
                }

                JOptionPane.showMessageDialog(this, details.toString(),
                        "Детали расчета", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    private void showCasterDetails(int resultId) {
        List<CasterResult> results = DatabaseService.getCasterHistory(currentUser);
        for (CasterResult result : results) {
            if (result.getId() == resultId) {
                StringBuilder details = new StringBuilder();
                details.append("Детали расчета МНЛЗ:\n\n");
                details.append("Марка стали: ").append(result.getSteelGrade()).append("\n");
                details.append("Масса плавки: ").append(result.getCastingWeight()).append(" т\n");
                details.append("Число ручьев: ").append(result.getNumberOfStreams()).append("\n");
                details.append("Металлургическая длина: ").append(result.getMetallurgicalLength()).append(" м\n");

                JOptionPane.showMessageDialog(this, details.toString(),
                        "Детали расчета", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    private void exportToPDF() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0) {
            String reportType = (String) tableModel.getValueAt(selectedRow, 1);
            int resultId = (Integer) tableModel.getValueAt(selectedRow, 0);

            if ("Раскисление".equals(reportType)) {
                exportAlloyingToPDF(resultId);
            } else if ("МНЛЗ".equals(reportType)) {
                exportCasterToPDF(resultId);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите запись для экспорта!");
        }
    }

    private void exportAlloyingToPDF(int resultId) {
        List<AlloyingResult> results = DatabaseService.getAlloyingHistory(currentUser);
        for (AlloyingResult result : results) {
            if (result.getId() == resultId) {
                PDFExportService.exportAlloyingToPDF(result, currentUser);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Запись не найдена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void exportCasterToPDF(int resultId) {
        List<CasterResult> results = DatabaseService.getCasterHistory(currentUser);
        for (CasterResult result : results) {
            if (result.getId() == resultId) {
                PDFExportService.exportCasterToPDF(result, currentUser);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Запись не найдена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void deleteSelected() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0) {
            String reportType = (String) tableModel.getValueAt(selectedRow, 1);
            int resultId = (Integer) tableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить выбранную запись?",
                    "Подтверждение удаления",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = false;
                if ("Раскисление".equals(reportType)) {
                    success = DatabaseService.deleteAlloyingResult(resultId);
                } else if ("МНЛЗ".equals(reportType)) {
                    success = DatabaseService.deleteCasterResult(resultId);
                }

                if (success) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Запись удалена!");
                } else {
                    JOptionPane.showMessageDialog(this, "Ошибка при удалении!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите запись для удаления!");
        }
    }

    private void showStatistics() {
        List<AlloyingResult> alloyingHistory = DatabaseService.getAlloyingHistory(currentUser);
        List<CasterResult> casterHistory = DatabaseService.getCasterHistory(currentUser);

        String stats = String.format(
                "СТАТИСТИКА РАСЧЕТОВ\n\n" +
                        "Всего расчетов: %d\n" +
                        "• Раскисление: %d\n" +
                        "• МНЛЗ: %d\n\n" +
                        "Последние расчеты сохранены в базе данных.",
                alloyingHistory.size() + casterHistory.size(),
                alloyingHistory.size(),
                casterHistory.size()
        );

        JOptionPane.showMessageDialog(this, stats, "Статистика", JOptionPane.INFORMATION_MESSAGE);
    }
}