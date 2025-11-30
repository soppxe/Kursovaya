package service;

import model.AlloyingResult;
import model.CasterResult;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Сервис для экспорта результатов расчетов в текстовые файлы формата PDF-like.
 * Предоставляет функциональность для создания структурированных отчетов 
 * по расчетам раскисления стали и параметров МНЛЗ.
 *
 * <p><b>Основные функции:</b>
 * <ul>
 * <li>Экспорт результатов расчета раскисления и легирования стали</li>
 * <li>Экспорт результатов расчета параметров машины непрерывного литья заготовок</li>
 * <li>Автоматическое формирование имен файлов с временными метками</li>
 * <li>Создание структурированных отчетов с разделами и рекомендациями</li>
 * <li>Отображение диалоговых окон с результатами операций</li>
 * </ul>
 *
 * <p><b>Формат отчетов:</b>
 * <ul>
 * <li>Текстовые файлы с расширением .txt</li>
 * <li>Структурированное представление данных с заголовками</li>
 * <li>Временные метки для уникальности файлов</li>
 * <li>Форматированные числовые значения</li>
 * </ul>
 *
 * @author Ваше имя
 * @version 1.0
 * @see AlloyingResult
 * @see CasterResult
 * @since 2024
 */
public class PDFExportService {

    /**
     * Экспортирует результаты расчета раскисления и легирования стали в текстовый файл.
     * Создает подробный отчет с химическими составами и расчетами добавок.
     *
     * <p><b>Содержание отчета:</b>
     * <ul>
     * <li>Заголовок и метаданные (дата, пользователь, марка стали)</li>
     * <li>Начальный химический состав стали</li>
     * <li>Целевой химический состав стали</li>
     * <li>Расчет необходимых материалов и их масс</li>
     * <li>Итоговый расчетный химический состав</li>
     * <li>Общая масса всех добавок</li>
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
     * PDFExportService.exportAlloyingToPDF(result, "user123");
     * // Создается файл: отчет_раскисление_20241215_143025.txt
     * }
     * </pre>
     *
     * @see AlloyingResult
     */
    public static void exportAlloyingToPDF(AlloyingResult result, String username) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = "отчет_раскисление_" + timestamp + ".txt";

            StringBuilder content = new StringBuilder();
            content.append("ОТЧЕТ ПО РАСЧЕТУ РАСКИСЛЕНИЯ И ЛЕГИРОВАНИЯ\n");
            content.append("==========================================\n\n");

            content.append("Дата расчета: ").append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date())).append("\n");
            content.append("Пользователь: ").append(username).append("\n");
            content.append("Марка стали: ").append(result.getSteelGrade()).append("\n");
            content.append("Масса плавки: ").append(result.getInitialWeight()).append(" кг\n\n");

            content.append("ХИМИЧЕСКИЙ СОСТАВ\n");
            content.append("=================\n\n");

            content.append("Начальный состав (%):\n");
            for (Map.Entry<String, Double> entry : result.getInitialComposition().entrySet()) {
                content.append(String.format("  %s: %.3f\n", entry.getKey(), entry.getValue()));
            }

            content.append("\nЦелевой состав (%):\n");
            for (Map.Entry<String, Double> entry : result.getTargetComposition().entrySet()) {
                content.append(String.format("  %s: %.3f\n", entry.getKey(), entry.getValue()));
            }

            content.append("\nНЕОБХОДИМЫЕ МАТЕРИАЛЫ\n");
            content.append("====================\n");

            double totalMaterials = 0;
            for (Map.Entry<String, Double> entry : result.getAddedMaterials().entrySet()) {
                content.append(String.format("  %s: %.2f кг\n", entry.getKey(), entry.getValue()));
                totalMaterials += entry.getValue();
            }

            if (result.getCarbonAdditive() > 0) {
                content.append(String.format("  Науглероживатель: %.2f кг\n", result.getCarbonAdditive()));
                totalMaterials += result.getCarbonAdditive();
            }

            content.append(String.format("\nОбщая масса добавок: %.2f кг\n", totalMaterials));

            content.append("\nРАСЧЕТНЫЙ ИТОГОВЫЙ СОСТАВ\n");
            content.append("========================\n");
            for (Map.Entry<String, Double> entry : result.getFinalComposition().entrySet()) {
                content.append(String.format("  %s: %.3f%%\n", entry.getKey(), entry.getValue()));
            }

            // Сохраняем в файл
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(content.toString());
            }

            JOptionPane.showMessageDialog(null,
                    "Отчет успешно экспортирован!\nФайл: " + filename,
                    "Экспорт завершен", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Ошибка при экспорте: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Экспортирует результаты расчета параметров машины непрерывного литья заготовок (МНЛЗ) в текстовый файл.
     * Создает отчет с техническими параметрами и рекомендациями по выбору типа МНЛЗ.
     *
     * <p><b>Содержание отчета:</b>
     * <ul>
     * <li>Заголовок и метаданные (дата, пользователь, марка стали)</li>
     * <li>Исходные данные для расчета</li>
     * <li>Расчетные параметры МНЛЗ (число ручьев, длины, радиусы)</li>
     * <li>Рекомендации по выбору типа МНЛЗ на основе радиуса</li>
     * <li>Максимальная протяженность жидкой фазы для каждого типа</li>
     * </ul>
     *
     * <p><b>Классификация МНЛЗ по радиусу:</b>
     * <ul>
     * <li>≤ 8 м - Вертикальная МНЛЗ</li>
     * <li>8-12 м - Радиальная с полным затвердеванием</li>
     * <li>12-20 м - Радиальная с двухфазным состоянием</li>
     * <li>> 20 м - Криволинейная с дифференцированным выпрямлением</li>
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
     * PDFExportService.exportCasterToPDF(result, "user123");
     * // Создается файл: отчет_мнлз_20241215_143025.txt
     * }
     * </pre>
     *
     * @see CasterResult
     */
    public static void exportCasterToPDF(CasterResult result, String username) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = "отчет_мнлз_" + timestamp + ".txt";

            StringBuilder content = new StringBuilder();
            content.append("ОТЧЕТ ПО РАСЧЕТУ ПАРАМЕТРОВ МНЛЗ\n");
            content.append("================================\n\n");

            content.append("Дата расчета: ").append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date())).append("\n");
            content.append("Пользователь: ").append(username).append("\n");
            content.append("Марка стали: ").append(result.getSteelGrade()).append("\n\n");

            content.append("ИСХОДНЫЕ ДАННЫЕ\n");
            content.append("===============\n");
            content.append(String.format("Масса плавки: %.1f т\n", result.getCastingWeight()));
            content.append(String.format("Сечение заготовки: %.2f × %.2f м\n", result.getSectionWidth(), result.getSectionThickness()));
            content.append(String.format("Скорость разливки: %.2f м/мин\n", result.getCastingSpeed()));
            content.append(String.format("Цикл разливки: %d мин\n", 60));

            content.append("\nРАСЧЕТНЫЕ ПАРАМЕТРЫ МНЛЗ\n");
            content.append("========================\n");
            content.append(String.format("Число ручьев: %d\n", result.getNumberOfStreams()));
            content.append(String.format("Металлургическая длина: %.2f м\n", result.getMetallurgicalLength()));
            content.append(String.format("Радиус МНЛЗ: %.2f м\n", result.getMachineRadius()));
            content.append(String.format("Высота МНЛЗ: %.2f м\n", result.getMachineHeight()));

            content.append("\nРЕКОМЕНДАЦИИ\n");
            content.append("============\n");

            double radius = result.getMachineRadius();
            if (radius <= 8) {
                content.append("Тип МНЛЗ: Вертикальная\n");
                content.append("Макс. протяженность жидкой фазы: 16 м\n");
            } else if (radius <= 12) {
                content.append("Тип МНЛЗ: Радиальная с полным затвердеванием\n");
                content.append("Макс. протяженность жидкой фазы: 19 м\n");
            } else if (radius <= 20) {
                content.append("Тип МНЛЗ: Радиальная с двухфазным состоянием\n");
                content.append("Макс. протяженность жидкой фазы: 30 м\n");
            } else {
                content.append("Тип МНЛЗ: Криволинейная с дифференцированным выпрямлением\n");
                content.append("Макс. протяженность жидкой фазы: 35 м\n");
            }

            // Сохраняем в файл
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(content.toString());
            }

            JOptionPane.showMessageDialog(null,
                    "Отчет успешно экспортирован!\nФайл: " + filename,
                    "Экспорт завершен", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Ошибка при экспорте: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}