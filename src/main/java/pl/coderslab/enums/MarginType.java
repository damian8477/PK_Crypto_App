package pl.coderslab.enums;

public enum MarginType {
    CROSSED("CRROSED"),
    ISOLATED("ISOLATED");

    private final String label;

    MarginType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}