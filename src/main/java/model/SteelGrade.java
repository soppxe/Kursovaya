package model;

public class SteelGrade {
    private int id;
    private String name;
    private double carbon;
    private double manganese;
    private double silicon;
    private double sulfur;
    private double phosphorus;
    private double chromium;
    private double nickel;
    private double molybdenum;
    private double aluminum;

    public SteelGrade() {}

    // Конструктор для готовой стали по ГОСТ
    public SteelGrade(String name, double carbon, double manganese, double silicon,
                      double sulfur, double phosphorus, double chromium,
                      double nickel, double molybdenum, double aluminum) {
        this.name = name;
        this.carbon = carbon;
        this.manganese = manganese;
        this.silicon = silicon;
        this.sulfur = sulfur;
        this.phosphorus = phosphorus;
        this.chromium = chromium;
        this.nickel = nickel;
        this.molybdenum = molybdenum;
        this.aluminum = aluminum;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getCarbon() { return carbon; }
    public void setCarbon(double carbon) { this.carbon = carbon; }
    public double getManganese() { return manganese; }
    public void setManganese(double manganese) { this.manganese = manganese; }
    public double getSilicon() { return silicon; }
    public void setSilicon(double silicon) { this.silicon = silicon; }
    public double getSulfur() { return sulfur; }
    public void setSulfur(double sulfur) { this.sulfur = sulfur; }
    public double getPhosphorus() { return phosphorus; }
    public void setPhosphorus(double phosphorus) { this.phosphorus = phosphorus; }
    public double getChromium() { return chromium; }
    public void setChromium(double chromium) { this.chromium = chromium; }
    public double getNickel() { return nickel; }
    public void setNickel(double nickel) { this.nickel = nickel; }
    public double getMolybdenum() { return molybdenum; }
    public void setMolybdenum(double molybdenum) { this.molybdenum = molybdenum; }
    public double getAluminum() { return aluminum; }
    public void setAluminum(double aluminum) { this.aluminum = aluminum; }
}