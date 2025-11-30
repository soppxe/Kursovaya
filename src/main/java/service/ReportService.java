package service;

import model.AlloyingResult;
import model.CasterResult;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Сервис для генерации отчетов по результатам расчетов в приложении "Калькулятор металлурга".
 * Предоставляет функциональность для создания текстовых отчетов и экспорта данных в CSV формат.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Генерация детальных отчетов по расчетам раскисления и легирования стали</li>
 * <li>Создание отчетов по расчетам параметров машины непрерывного литья заготовок (МНЛЗ)</li>
 * <li>Экспорт исторических данных расчетов в CSV формат для дальнейшего анализа</li>
 * <li>Автоматическое формирование имен файлов с временными метками</li>
 * <li>Интерактивное уведомление пользователя о результатах операций</li>
 * </ul>
 *
 * <p><b>Форматы отчетов:</b>
 * <ul>
 * <li>Текстовые файлы (.txt) - детальные отчеты с форматированием</li>
 * <li>CSV файлы (.csv) - табличные данные для анализа в Excel и других программах</li>
 * </ul>
 *
 * @author Саитова София
 * @version 1.0
 * @see AlloyingResult
 * @see CasterResult
 * @since 2025
 */
public class ReportService {

    /**
     * Генерирует детальный отчет по расчету раскисления и легирования стали.
     * Создает текстовый файл с полной информацией о химических составах и расчетных материалах.
     *
     * <p><b>Содержание отчета:</b>
     * <ul>
     * <li>Метаданные расчета (дата, пользователь, марка стали, масса плавки)</li>
     * <li>Начальный химический состав стали с содержанием элементов в процентах</li>
     * <li>Целевой химический состав стали</li>
     * <li>Расчетные количества необходимых материалов в килограммах</li>
     * <li>Информация о науглероживателе (если применялся)</li>
     * </ul>
     *
     * <p><b>Имя файла:</b> "отчет_раскисление_ГГГГММДД_ЧЧММСС.txt"
     *
     * @param result объект AlloyingResult с результатами расчета раскисления
     * @param username имя пользователя, выполнившего расчет
     *
     * @throws IOException если произошла ошибка при записи файла
     *
     * @example
     * <pre>
     * {@code
     * AlloyingResult result = // результат расчета
     * ReportService.generateAlloyingReport(result, "user123");
     * // Создается файл: отчет_раскисление_20241215_143025.txt
     * // Показывается диалоговое окно об успешном сохранении
     * }
     * </pre>
     *
     * @see AlloyingResult
     */
    public static void generateAlloyingReport(AlloyingResult result, String username) {
        StringBuilder report = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        report.append("ОТЧЕТ ПО РАСЧЕТУ РАСКИСЛЕНИЯ И ЛЕГИРОВАНИЯ\n");
        report.append("==========================================\n\n");

        report.append("Дата расчета: ").append(sdf.format(new Date())).append("\n");
        report.append("Пользователь: ").append(username).append("\n");
        report.append("Марка стали: ").append(result.getSteelGrade()).append("\n");
        report.append("Масса плавки: ").append(result.getInitialWeight()).append(" кг\n\n");

        report.append("ХИМИЧЕСКИЙ СОСТАВ:\n");
        report.append("Начальный состав (%):\n");
        for (var entry : result.getInitialComposition().entrySet()) {
            report.append(String.format("  %s: %.3f\n", entry.getKey(), entry.getValue()));
        }

        report.append("\nЦелевой состав (%):\n");
        for (var entry : result.getTargetComposition().entrySet()) {
            report.append(String.format("  %s: %.3f\n", entry.getKey(), entry.getValue()));
        }

        report.append("\nРАСЧЕТНЫЕ МАТЕРИАЛЫ:\n");
        for (var entry : result.getAddedMaterials().entrySet()) {
            report.append(String.format("  %s: %.2f кг\n", entry.getKey(), entry.getValue()));
        }

        if (result.getCarbonAdditive() > 0) {
            report.append(String.format("  Науглероживатель: %.2f кг\n", result.getCarbonAdditive()));
        }

        // Сохраняем в файл
        try (FileWriter writer = new FileWriter("отчет_раскисление_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt")) {
            writer.write(report.toString());
            JOptionPane.showMessageDialog(null, "Отчет сохранен в файл!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка сохранения отчета: " + e.getMessage());
        }
    }

    /**
     * Генерирует отчет по расчету параметров машины непрерывного литья заготовок (МНЛЗ).
     * Создает текстовый файл с техническими параметрами и характеристиками МНЛЗ.
     *
     * <p><b>Содержание отчета:</b>
     * <ul>
     * <li>Метаданные расчета (дата, пользователь, марка стали)</li>
     * <li>Основные параметры разливки (масса, сечение заготовки, скорость)</li>
     * <li>Расчетные параметры МНЛЗ (число ручьев, металлургическая длина)</li>
     * <li>Геометрические характеристики (радиус, высота машины)</li>
     * </ul>
     *
     * <p><b>Имя файла:</b> "отчет_мнлз_ГГГГММДД_ЧЧММСС.txt"
     *
     * @param result объект CasterResult с результатами расчета параметров МНЛЗ
     * @param username имя пользователя, выполнившего расчет
     *
     * @throws IOException если произошла ошибка при записи файла
     *
     * @example
     * <pre>
     * {@code
     * CasterResult result = // результат расчета МНЛЗ
     * ReportService.generateCasterReport(result, "user123");
     * // Создается файл: отчет_мнлз_20241215_143025.txt
     * // Показывается диалоговое окно об успешном сохранении
     * }
     * </pre>
     *
     * @see CasterResult
     */
    public static void generateCasterReport(CasterResult result, String username) {
        StringBuilder report = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        report.append("ОТЧЕТ ПО РАСЧЕТУ ПАРАМЕТРОВ МНЛЗ\n");
        report.append("================================\n\n");

        report.append("Дата расчета: ").append(sdf.format(new Date())).append("\n");
        report.append("Пользователь: ").append(username).append("\n");
        report.append("Марка стали: ").append(result.getSteelGrade()).append("\n\n");

        report.append("ПАРАМЕТРЫ МНЛЗ:\n");
        report.append(String.format("  Масса плавки: %.1f т\n", result.getCastingWeight()));
        report.append(String.format("  Сечение заготовки: %.2f × %.2f м\n",
                result.getSectionWidth(), result.getSectionThickness()));
        report.append(String.format("  Число ручьев: %d\n", result.getNumberOfStreams()));
        report.append(String.format("  Металлургическая длина: %.2f м\n", result.getMetallurgicalLength()));
        report.append(String.format("  Радиус МНЛЗ: %.2f м\n", result.getMachineRadius()));
        report.append(String.format("  Высота МНЛЗ: %.2f м\n", result.getMachineHeight()));
        report.append(String.format("  Скорость разливки: %.2f м/мин\n", result.getCastingSpeed()));

        // Сохраняем в файл
        try (FileWriter writer = new FileWriter("отчет_мнлз_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt")) {
            writer.write(report.toString());
            JOptionPane.showMessageDialog(null, "Отчет сохранен в файл!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка сохранения отчета: " + e.getMessage());
        }
    }

    /**
     * Экспортирует список результатов расчетов раскисления в CSV формат.
     * Создает табличный файл, пригодный для импорта в Excel и другие программы анализа данных.
     *
     * <p><b>Структура CSV файла:</b>
     * <ul>
     * <li>Заголовок с названиями колонок на русском языке</li>
     * <li>Разделитель - точка с запятой (;)</li>
     * <li>Кодировка - UTF-8</li>
     * </ul>
     *
     * <p><b>Колонки данных:</b>
     * <ul>
     * <li>ID - идентификатор расчета</li>
     * <li>Марка стали - наименование марки стали</li>
     * <li>Масса плавки - начальная масса в килограммах</li>
     * <li>Ферромарганец - масса ферромарганца в килограммах</li>
     * <li>Ферросилиций - масса ферросилиция в килограммах</li>
     * <li>Алюминий - масса алюминия в килограммах</li>
     * <li>Науглероживатель - масса науглероживателя в килограммах</li>
     * </ul>
     *
     * @param results список объектов AlloyingResult для экспорта
     * @param filename имя файла для сохранения (рекомендуется использовать расширение .csv)
     *
     * @throws IOException если произошла ошибка при записи файла
     *
     * @example
     * <pre>
     * {@code
     * List<AlloyingResult> history = DatabaseService.getAlloyingHistory("user123");
     * ReportService.exportToCSV(history, "история_раскисления.csv");
     * // Создается CSV файл с данными всех расчетов пользователя
     * // Показывается диалоговое окно об успешном экспорте
     * }
     * </pre>
     *
     * @see AlloyingResult
     * @see java.util.List
     */
    public static void exportToCSV(List<AlloyingResult> results, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("ID;Марка стали;Масса плавки;Ферромарганец;Ферросилиций;Алюминий;Науглероживатель\n");

            for (AlloyingResult result : results) {
                writer.write(String.format("%d;%s;%.0f;%.2f;%.2f;%.2f;%.2f\n",
                        result.getId(),
                        result.getSteelGrade(),
                        result.getInitialWeight(),
                        result.getAddedMaterials().getOrDefault("Ферромарганец ФМн78", 0.0),
                        result.getAddedMaterials().getOrDefault("Ферросилиций ФС65", 0.0),
                        result.getAddedMaterials().getOrDefault("Алюминий первичный", 0.0),
                        result.getCarbonAdditive()
                ));
            }

            JOptionPane.showMessageDialog(null, "Данные экспортированы в " + filename);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка экспорта: " + e.getMessage());
        }
    }
}