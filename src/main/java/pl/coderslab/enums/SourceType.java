package pl.coderslab.enums;

public enum SourceType {
    TELEGRAM("TELEGRAM"),
    OWN("OWN"),
    STRATEGY("STRATEGY");

    private final String label;

    SourceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
