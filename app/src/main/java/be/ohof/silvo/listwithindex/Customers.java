package be.ohof.silvo.listwithindex;

public class Customers {
    private String name;
    private String description;
    private String vat;
    private String extension;

    public Customers(String name, String description, String vat, String extension) {
        this.name = name;
        this.description = description;
        this.vat = vat;
        this.extension = extension;
    }

    public String toString() {
        return String.format("%s \t Description: %s\tVAT: %s\tExt: %d", this.name, this.description, this.vat, this.extension);
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

}
