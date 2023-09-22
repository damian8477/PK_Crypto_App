package pl.coderslab.binance.client.model.enums;


public enum OrderSide {
    BUY("BUY"),
    SELL("SELL"),
    BUY_HEDGE("LONG&side=BUY"),
    BUY_CLOSE_HEDGE("LONG&side=SELL"),
    SHORT_HEDGE("SHORT&side=SELL"),
    SHORT_CLOSE_HEDGE("SHORT&side=BUY");

    private final String code;

    OrderSide(String side) {
        this.code = side;
    }

    @Override
    public String toString() {
        return code;
    }


}