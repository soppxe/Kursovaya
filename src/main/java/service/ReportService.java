package service;

import model.AlloyingResult;
import model.CasterResult;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportService {

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