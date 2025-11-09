package model;

import java.util.HashMap;
import java.util.Map;

public class AlloyingResult {
    private int id;
    private String steelGrade;
    private double initialWeight;
    private Map<String, Double> initialComposition;
    private Map<String, Double> targetComposition;
    private Map<String, Double> addedMaterials;
    private Map<String, Double> finalComposition;
    private double carbonAdditive;

    public AlloyingResult() {
        this.initialComposition = new HashMap<>();
        this.targetComposition = new HashMap<>();
        this.addedMaterials = new HashMap<>();
        this.finalComposition = new HashMap<>();
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSteelGrade() { return steelGrade; }
    public void setSteelGrade(String steelGrade) { this.steelGrade = steelGrade; }
    public double getInitialWeight() { return initialWeight; }
    public void setInitialWeight(double initialWeight) { this.initialWeight = initialWeight; }
    public Map<String, Double> getInitialComposition() { return initialComposition; }
    public void setInitialComposition(Map<String, Double> initialComposition) { this.initialComposition = initialComposition; }
    public Map<String, Double> getTargetComposition() { return targetComposition; }
    public void setTargetComposition(Map<String, Double> targetComposition) { this.targetComposition = targetComposition; }
    public Map<String, Double> getAddedMaterials() { return addedMaterials; }
    public void setAddedMaterials(Map<String, Double> addedMaterials) { this.addedMaterials = addedMaterials; }
    public Map<String, Double> getFinalComposition() { return finalComposition; }
    public void setFinalComposition(Map<String, Double> finalComposition) { this.finalComposition = finalComposition; }
    public double getCarbonAdditive() { return carbonAdditive; }
    public void setCarbonAdditive(double carbonAdditive) { this.carbonAdditive = carbonAdditive; }
}