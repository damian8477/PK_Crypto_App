package pl.coderslab.enums;

public enum CashType {
    LOT("Lot"),
    DOLAR("$"),
    PERCENT("% konta");

    private final String label;

    CashType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
