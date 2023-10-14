package pl.coderslab.enums;

public enum Action {
    OPEN("open"),
    CLOSE("close"),
    UPDATE("update");

    private final String label;

    Action(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
