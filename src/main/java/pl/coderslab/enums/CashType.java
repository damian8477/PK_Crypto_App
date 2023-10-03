package pl.coderslab.enums;

public enum CashType {
    LOT("Lot"),
    VALUE2("$"),
    VALUE3("% konta");

    private final String label;

    CashType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
