package model;

public class CasterResult {
    private int id;
    private String steelGrade;
    private double castingWeight;
    private double sectionWidth;
    private double sectionThickness;
    private int numberOfStreams;
    private double metallurgicalLength;
    private double machineRadius;
    private double machineHeight;
    private double castingSpeed;

    public CasterResult() {}

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSteelGrade() { return steelGrade; }
    public void setSteelGrade(String steelGrade) { this.steelGrade = steelGrade; }
    public double getCastingWeight() { return castingWeight; }
    public void setCastingWeight(double castingWeight) { this.castingWeight = castingWeight; }
    public double getSectionWidth() { return sectionWidth; }
    public void setSectionWidth(double sectionWidth) { this.sectionWidth = sectionWidth; }
    public double getSectionThickness() { return sectionThickness; }
    public void setSectionThickness(double sectionThickness) { this.sectionThickness = sectionThickness; }
    public int getNumberOfStreams() { return numberOfStreams; }
    public void setNumberOfStreams(int numberOfStreams) { this.numberOfStreams = numberOfStreams; }
    public double getMetallurgicalLength() { return metallurgicalLength; }
    public void setMetallurgicalLength(double metallurgicalLength) { this.metallurgicalLength = metallurgicalLength; }
    public double getMachineRadius() { return machineRadius; }
    public void setMachineRadius(double machineRadius) { this.machineRadius = machineRadius; }
    public double getMachineHeight() { return machineHeight; }
    public void setMachineHeight(double machineHeight) { this.machineHeight = machineHeight; }
    public double getCastingSpeed() { return castingSpeed; }
    public void setCastingSpeed(double castingSpeed) { this.castingSpeed = castingSpeed; }
}