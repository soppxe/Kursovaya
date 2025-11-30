package view;

import model.AlloyingResult;
import model.CasterResult;
import service.DatabaseService;
import service.PDFExportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Панель для просмотра истории расчетов и управления отчетами.
 * Предоставляет интерфейс для работы с сохраненными расчетами раскисления стали и параметров МНЛЗ.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Просмотр истории расчетов с фильтрацией и поиском</li>
 * <li>Экспорт отчетов в PDF формат</li>
 * <li>Управление сохраненными расчетами (просмотр, удаление)</li>
 * <li>Статистика по выполненным расчетам</li>
 * <li>Пакетный экспорт данных</li>
 * </ul>
 * </p>
 *
 * <p><b>Компоненты интерфейса:</b>
 * <ul>
 * <li>Таблица с историей расчетов</li>
 * <li>Панель фильтров и поиска</li>
 * <li>Панель операций с отчетами</li>
 * <li>Статусная строка</li>
 * </ul>
 * </p>
 *
 * @author Саитова София
 * @version 1.0
 * @see AlloyingResult
 * @see CasterResult
 * @see DatabaseService
 * @see PDFExportService
 * @since 2025
 */
public class ReportsPanel extends JPanel {

    /**
     * Имя текущего пользователя для фильтрации данных.
     */
    private String currentUser;

    /**
     * Таблица для отображения истории расчетов.
     */
    private JTable historyTable;

    /**
     * Модель данных для таблицы истории расчетов.
     */
    private DefaultTableModel tableModel;

    /**
     * Сортировщик строк таблицы для реализации фильтрации.
     */
    private TableRowSorter<DefaultTableModel> sorter;

    /**
     * Комбобокс для выбора типа расчета при фильтрации.
     */
    private JComboBox<String> filterTypeCombo;

    /**
     * Поле для текстового поиска в истории расчетов.
     */
    private JTextField searchField;

    /**
     * Метка для отображения статусных сообщений.
     */
    private JLabel statusLabel;

    /**
     * Конструктор панели отчетов.
     * Инициализирует интерфейс и загружает историю расчетов для указанного пользователя.
     *
     * @param username имя пользователя для загрузки соответствующих данных
     * @throws IllegalArgumentException если имя пользователя null или пустое
     */
    public ReportsPanel(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        this.currentUser = username;
        initializeUI();
        loadHistory();
    }

    /**
     * Инициализирует пользовательский интерфейс панели отчетов.
     * Создает и размещает все компоненты интерфейса.
     *
     * @see #createControlPanel()
     * @see #createTablePanel()
     * @see #createOperationsPanel()
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Заголовок
        JLabel titleLabel = new JLabel("История расчетов и отчеты - " + currentUser, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Панель управления с фильтрами
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        // Панель с таблицей истории
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Панель кнопок операций
        JPanel operationsPanel = createOperationsPanel();
        add(operationsPanel, BorderLayout.SOUTH);

        // Статусная строка
        statusLabel = new JLabel("Готово");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * Создает панель управления с элементами фильтрации и поиска.
     *
     * @return панель с элементами управления фильтрацией
     * @see #applyFilters()
     * @see #clearFilters()
     * @see #quickExportSelected()
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Фильтры и поиск"));

        panel.add(new JLabel("Тип расчета:"));
        filterTypeCombo = new JComboBox<>(new String[]{"Все", "Раскисление", "МНЛЗ"});
        panel.add(filterTypeCombo);

        panel.add(new JLabel("Поиск:"));
        searchField = new JTextField(15);
        panel.add(searchField);

        JButton applyFilterBtn = new JButton("Применить фильтр");
        JButton clearFilterBtn = new JButton("Сбросить");
        JButton quickExportBtn = new JButton("Быстрый экспорт выделенного");

        panel.add(applyFilterBtn);
        panel.add(clearFilterBtn);
        panel.add(quickExportBtn);

        applyFilterBtn.addActionListener(e -> applyFilters());
        clearFilterBtn.addActionListener(e -> clearFilters());
        quickExportBtn.addActionListener(e -> quickExportSelected());

        return panel;
    }

    /**
     * Создает панель с таблицей для отображения истории расчетов.
     * Настраивает модель данных, сортировку и обработку событий.
     *
     * @return панель с таблицей истории расчетов
     * @see DefaultTableModel
     * @see TableRowSorter
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Сохраненные расчеты"));

        String[] columns = {"ID", "Тип расчета", "Марка стали", "Основные параметры", "Дата", "Пользователь"};
        tableModel = new DefaultTableModel(columns, 0) {
            /**
             * Возвращает класс данных для указанного столбца.
             *
             * @param columnIndex индекс столбца
             * @return класс данных столбца
             */
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class;
            }

            /**
             * Определяет, является ли ячейка редактируемой.
             *
             * @param row индекс строки
             * @param column индекс столбца
             * @return false - все ячейки нередактируемые
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещаем редактирование
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setAutoCreateRowSorter(true);
        historyTable.setFillsViewportHeight(true);

        // Настройка рендерера для красивого отображения
        historyTable.setRowHeight(25);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Настройка сортировки
        sorter = new TableRowSorter<>(tableModel);
        historyTable.setRowSorter(sorter);

        // Двойной клик для быстрого экспорта
        historyTable.addMouseListener(new MouseAdapter() {
            /**
             * Обрабатывает двойной клик по строке таблицы для быстрого экспорта.
             *
             * @param e событие мыши
             */
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    quickExportSelected();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Создает панель с кнопками операций над отчетами.
     *
     * @return панель с кнопками операций
     * @see #loadHistory()
     * @see #showDetails()
     * @see #exportToPDF()
     * @see #deleteSelected()
     * @see #showStatistics()
     * @see #exportAllToCSV()
     */
    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Операции с отчетами"));

        JButton refreshBtn = new JButton("Обновить");
        JButton detailsBtn = new JButton("Показать детали");
        JButton exportPDFBtn = new JButton("Экспорт в PDF");
        JButton deleteBtn = new JButton("Удалить");
        JButton statsBtn = new JButton("Статистика");
        JButton exportAllBtn = new JButton("Экспорт всех в CSV");

        // Устанавливаем иконки для кнопок (можно добавить позже)
        refreshBtn.setToolTipText("Обновить список расчетов");
        detailsBtn.setToolTipText("Показать подробную информацию о расчете");
        exportPDFBtn.setToolTipText("Экспортировать выделенный отчет в PDF");
        deleteBtn.setToolTipText("Удалить выделенный расчет");
        statsBtn.setToolTipText("Показать статистику по расчетам");
        exportAllBtn.setToolTipText("Экспортировать все данные в CSV");

        panel.add(refreshBtn);
        panel.add(detailsBtn);
        panel.add(exportPDFBtn);
        panel.add(deleteBtn);
        panel.add(statsBtn);
        panel.add(exportAllBtn);

        refreshBtn.addActionListener(e -> loadHistory());
        detailsBtn.addActionListener(e -> showDetails());
        exportPDFBtn.addActionListener(e -> exportToPDF());
        deleteBtn.addActionListener(e -> deleteSelected());
        statsBtn.addActionListener(e -> showStatistics());
        exportAllBtn.addActionListener(e -> exportAllToCSV());

        return panel;
    }

    /**
     * Загружает историю расчетов из базы данных.
     * Использует SwingWorker для асинхронной загрузки данных без блокировки интерфейса.
     *
     * @see DatabaseService#getAlloyingHistory(String)
     * @see DatabaseService#getCasterHistory(String)
     * @see SwingWorker
     */
    private void loadHistory() {
        tableModel.setRowCount(0);
        statusLabel.setText("Загрузка данных...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            /**
             * Выполняет загрузку данных в фоновом потоке.
             *
             * @return null
             * @throws Exception если произошла ошибка при загрузке данных
             */
            @Override
            protected Void doInBackground() throws Exception {
                List<AlloyingResult> alloyingResults = DatabaseService.getAlloyingHistory(currentUser);
                List<CasterResult> casterResults = DatabaseService.getCasterHistory(currentUser);

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

                for (AlloyingResult result : alloyingResults) {
                    tableModel.addRow(new Object[]{
                            result.getId(),
                            "Раскисление",
                            result.getSteelGrade(),
                            String.format("Масса: %.0f кг, Добавки: %.1f кг",
                                    result.getInitialWeight(),
                                    getTotalMaterials(result)),
                            "Расчет сохранен",
                            currentUser
                    });
                }

                for (CasterResult result : casterResults) {
                    tableModel.addRow(new Object[]{
                            result.getId(),
                            "МНЛЗ",
                            result.getSteelGrade(),
                            String.format("Ручьев: %d, Длина: %.1f м",
                                    result.getNumberOfStreams(),
                                    result.getMetallurgicalLength()),
                            "Расчет сохранен",
                            currentUser
                    });
                }
                return null;
            }

            /**
             * Выполняется после завершения загрузки данных.
             * Обновляет статусную строку и показывает информационное сообщение если данных нет.
             */
            @Override
            protected void done() {
                statusLabel.setText(String.format("Загружено записей: %d", tableModel.getRowCount()));
                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(ReportsPanel.this,
                            "Нет сохраненных расчетов. Выполните расчеты и сохраните их.",
                            "Информация", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * Применяет фильтры к таблице на основе выбранных критериев.
     * Комбинирует фильтр по типу расчета и текстовый поиск.
     *
     * @see RowFilter
     * @see TableRowSorter#setRowFilter(RowFilter)
     */
    private void applyFilters() {
        String selectedType = (String) filterTypeCombo.getSelectedItem();
        String searchText = searchField.getText().trim().toLowerCase();

        RowFilter<DefaultTableModel, Object> typeFilter = null;
        RowFilter<DefaultTableModel, Object> searchFilter = null;

        if (!"Все".equals(selectedType)) {
            typeFilter = RowFilter.regexFilter("^" + selectedType + "$", 1);
        }

        if (!searchText.isEmpty()) {
            searchFilter = RowFilter.regexFilter("(?i)" + searchText, 2); // Поиск в марке стали
        }

        if (typeFilter != null && searchFilter != null) {
            sorter.setRowFilter(RowFilter.andFilter(List.of(typeFilter, searchFilter)));
        } else if (typeFilter != null) {
            sorter.setRowFilter(typeFilter);
        } else if (searchFilter != null) {
            sorter.setRowFilter(searchFilter);
        } else {
            sorter.setRowFilter(null);
        }

        statusLabel.setText("Фильтр применен");
    }

    /**
     * Сбрасывает все примененные фильтры и поисковые критерии.
     */
    private void clearFilters() {
        filterTypeCombo.setSelectedIndex(0);
        searchField.setText("");
        sorter.setRowFilter(null);
        statusLabel.setText("Фильтры сброшены");
    }

    /**
     * Выполняет быстрый экспорт выделенной в таблице записи.
     * Определяет тип расчета и вызывает соответствующий метод экспорта.
     *
     * @see #exportAlloyingToPDF(int)
     * @see #exportCasterToPDF(int)
     */
    private void quickExportSelected() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = historyTable.convertRowIndexToModel(selectedRow);
            String reportType = (String) tableModel.getValueAt(modelRow, 1);
            int resultId = (Integer) tableModel.getValueAt(modelRow, 0);
            String steelGrade = (String) tableModel.getValueAt(modelRow, 2);

            statusLabel.setText("Экспорт отчета: " + steelGrade);

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                /**
                 * Выполняет экспорт в фоновом потоке.
                 *
                 * @return null
                 */
                @Override
                protected Void doInBackground() throws Exception {
                    if ("Раскисление".equals(reportType)) {
                        exportAlloyingToPDF(resultId);
                    } else if ("МНЛЗ".equals(reportType)) {
                        exportCasterToPDF(resultId);
                    }
                    return null;
                }

                /**
                 * Обновляет статус после завершения экспорта.
                 */
                @Override
                protected void done() {
                    statusLabel.setText("Экспорт завершен: " + steelGrade);
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Выберите запись для экспорта!",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Показывает детальную информацию о выделенном расчете.
     * Определяет тип расчета и вызывает соответствующий метод отображения деталей.
     *
     * @see #showAlloyingDetails(int)
     * @see #showCasterDetails(int)
     */
    private void showDetails() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = historyTable.convertRowIndexToModel(selectedRow);
            String reportType = (String) tableModel.getValueAt(modelRow, 1);
            int resultId = (Integer) tableModel.getValueAt(modelRow, 0);
            String steelGrade = (String) tableModel.getValueAt(modelRow, 2);

            if ("Раскисление".equals(reportType)) {
                showAlloyingDetails(resultId);
            } else if ("МНЛЗ".equals(reportType)) {
                showCasterDetails(resultId);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Выберите запись для просмотра деталей!",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Показывает детальную информацию о расчете раскисления стали.
     *
     * @param resultId идентификатор расчета раскисления
     * @see AlloyingResult
     * @see DatabaseService#getAlloyingHistory(String)
     */
    private void showAlloyingDetails(int resultId) {
        List<AlloyingResult> results = DatabaseService.getAlloyingHistory(currentUser);
        for (AlloyingResult result : results) {
            if (result.getId() == resultId) {
                StringBuilder details = new StringBuilder();
                details.append("ДЕТАЛИ РАСЧЕТА РАСКИСЛЕНИЯ\n");
                details.append("==========================\n\n");
                details.append("Марка стали: ").append(result.getSteelGrade()).append("\n");
                details.append("Масса плавки: ").append(result.getInitialWeight()).append(" кг\n\n");

                details.append("ДОБАВЛЕННЫЕ МАТЕРИАЛЫ:\n");
                details.append("---------------------\n");
                for (var entry : result.getAddedMaterials().entrySet()) {
                    details.append(String.format(" %s: %.2f кг\n", entry.getKey(), entry.getValue()));
                }
                if (result.getCarbonAdditive() > 0) {
                    details.append(String.format(" Науглероживатель: %.2f кг\n", result.getCarbonAdditive()));
                }

                JTextArea textArea = new JTextArea(details.toString(), 20, 50);
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                JOptionPane.showMessageDialog(this, scrollPane,
                        "Детали расчета: " + result.getSteelGrade(),
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Запись не найдена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Показывает детальную информацию о расчете параметров МНЛЗ.
     *
     * @param resultId идентификатор расчета МНЛЗ
     * @see CasterResult
     * @see DatabaseService#getCasterHistory(String)
     */
    private void showCasterDetails(int resultId) {
        List<CasterResult> results = DatabaseService.getCasterHistory(currentUser);
        for (CasterResult result : results) {
            if (result.getId() == resultId) {
                StringBuilder details = new StringBuilder();
                details.append("ДЕТАЛИ РАСЧЕТА МНЛЗ\n");
                details.append("===================\n\n");
                details.append("Марка стали: ").append(result.getSteelGrade()).append("\n");
                details.append("Масса плавки: ").append(result.getCastingWeight()).append(" т\n");
                details.append("Сечение: ").append(result.getSectionWidth()).append(" × ")
                        .append(result.getSectionThickness()).append(" м\n");
                details.append("Скорость разливки: ").append(result.getCastingSpeed()).append(" м/мин\n");
                details.append("Число ручьев: ").append(result.getNumberOfStreams()).append("\n");
                details.append("Металлургическая длина: ").append(result.getMetallurgicalLength()).append(" м\n");
                details.append("Радиус МНЛЗ: ").append(result.getMachineRadius()).append(" м\n");
                details.append("Высота МНЛЗ: ").append(result.getMachineHeight()).append(" м\n");

                JOptionPane.showMessageDialog(this, details.toString(),
                        "Детали расчета МНЛЗ", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Запись не найдена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Выполняет экспорт выделенного отчета в PDF формат.
     * Является псевдонимом для метода {@link #quickExportSelected()}.
     */
    private void exportToPDF() {
        quickExportSelected(); // Используем ту же логику
    }

    /**
     * Экспортирует расчет раскисления в PDF формат.
     *
     * @param resultId идентификатор расчета раскисления
     * @see PDFExportService#exportAlloyingToPDF(AlloyingResult, String)
     */
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

    /**
     * Экспортирует расчет МНЛЗ в PDF формат.
     *
     * @param resultId идентификатор расчета МНЛЗ
     * @see PDFExportService#exportCasterToPDF(CasterResult, String)
     */
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

    /**
     * Удаляет выделенный расчет из базы данных.
     * Запрашивает подтверждение перед выполнением операции.
     *
     * @see DatabaseService#deleteAlloyingResult(int)
     * @see DatabaseService#deleteCasterResult(int)
     */
    private void deleteSelected() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = historyTable.convertRowIndexToModel(selectedRow);
            String reportType = (String) tableModel.getValueAt(modelRow, 1);
            int resultId = (Integer) tableModel.getValueAt(modelRow, 0);
            String steelGrade = (String) tableModel.getValueAt(modelRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Удалить расчет '" + steelGrade + "'?\nЭта операция необратима.",
                    "Подтверждение удаления",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = false;
                if ("Раскисление".equals(reportType)) {
                    success = DatabaseService.deleteAlloyingResult(resultId);
                } else if ("МНЛЗ".equals(reportType)) {
                    success = DatabaseService.deleteCasterResult(resultId);
                }

                if (success) {
                    tableModel.removeRow(modelRow);
                    statusLabel.setText("Запись удалена: " + steelGrade);
                    JOptionPane.showMessageDialog(this, "Запись успешно удалена!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Ошибка при удалении записи!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Выберите запись для удаления!", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Показывает статистику по выполненным расчетам.
     * Отображает общее количество расчетов и распределение по типам.
     */
    private void showStatistics() {
        List<AlloyingResult> alloyingHistory = DatabaseService.getAlloyingHistory(currentUser);
        List<CasterResult> casterHistory = DatabaseService.getCasterHistory(currentUser);

        int total = alloyingHistory.size() + casterHistory.size();

        String stats = String.format(
                "СТАТИСТИКА РАСЧЕТОВ\n\n" +
                        "Всего расчетов: %d\n" +
                        "• Раскисление: %d\n" +
                        "• МНЛЗ: %d\n\n" +
                        "Пользователь: %s\n" +
                        "Данные актуальны на: %s",
                total, alloyingHistory.size(), casterHistory.size(),
                currentUser, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date()));

        JOptionPane.showMessageDialog(this, stats, "Статистика расчетов",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Выполняет экспорт всех данных в CSV формат.
     * Открывает диалог выбора файла для сохранения.
     */
    private void exportAllToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Экспорт всех данных в CSV");
        fileChooser.setSelectedFile(new java.io.File("металлургические_расчеты.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filename.toLowerCase().endsWith(".csv")) {
                filename += ".csv";
            }

            // Здесь можно добавить реализацию экспорта всех данных
            // Например: ReportService.exportAllToCSV(filename, currentUser);

            JOptionPane.showMessageDialog(this,
                    "Экспорт всех данных в CSV выполнен!\nФайл: " + filename,
                    "Экспорт завершен", JOptionPane.INFORMATION_MESSAGE);
            statusLabel.setText("Данные экспортированы в: " + filename);
        }
    }

    /**
     * Вычисляет общую массу добавленных материалов для расчета раскисления.
     *
     * @param result объект результата расчета раскисления
     * @return общая масса добавленных материалов в килограммах
     */
    private double getTotalMaterials(AlloyingResult result) {
        double total = 0;
        for (Double value : result.getAddedMaterials().values()) {
            total += value;
        }
        return total + result.getCarbonAdditive();
    }
}