package pl.coderslab.binance.client.model.enums;


public enum BalanceMode {


    AVAILABLE("0"),


    TOTAL("1");

    private final String code;

    BalanceMode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
