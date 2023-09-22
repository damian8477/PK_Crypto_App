package pl.coderslab.binance.client.model.enums;


public enum AccountChangeModeEnum {


    BALANCE("0"),


    TOTAL("1");

    private final String code;

    AccountChangeModeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
