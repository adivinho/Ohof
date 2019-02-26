package be.ohof.silvo.listwithindex;

public class Customers {
    private String name;
    private String description;
    private String vat;
    private String extension;
    private String rack;

    public Customers(String name, String description, String vat, String extension, String rack) {
        this.name = name;
        this.description = description;
        this.vat = vat;
        this.extension = extension;
        this.rack = rack;
    }

    public String toString() {
        return String.format("%s \t Description: %s\tVAT: %s\tExt: %d\tRack: %s", this.name, this.description, this.vat, this.extension, this.rack);
    }

    public String getName() {
        return String.format("%s", this.name);
    }

    public String getDescription() {
        return String.format("%s", this.description);
    }
    public String getVAT() {
        return String.format("%s", this.vat);
    }

    public String getExtension() {
        return String.format("%s", this.extension);
    }
    public String getRack() { return String.format("%s", this.rack);}

}
