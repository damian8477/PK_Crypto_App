package pl.coderslab.enums;

public enum TokenType {
    PASSWORD("password"),
    EMAIL("email");

    private final String label;

    TokenType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
