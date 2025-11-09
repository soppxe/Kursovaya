package service;

import model.AlloyingResult;
import model.CasterResult;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PDFExportService {

    /**
     * Экспорт реальных результатов раскисления
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
     * Экспорт реальных результатов МНЛЗ
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