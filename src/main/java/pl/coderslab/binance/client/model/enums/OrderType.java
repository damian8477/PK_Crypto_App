package pl.coderslab.binance.client.model.enums;


import pl.coderslab.binance.client.impl.utils.EnumLookup;

public enum OrderType {
    LIMIT("LIMIT"),
    MARKET("MARKET"),
    STOP("STOP"),
    STOP_LIMIT("STOP_LIMIT"),
    STOP_LOSS_LIMIT("STOP_LOSS_LIMIT"),
    STOP_MARKET("STOP_MARKET"),
    TAKE_PROFIT("TAKE_PROFIT"),
    TAKE_PROFIT_MARKET("TAKE_PROFIT_MARKET"),
    INVALID(null);

    private static final EnumLookup<OrderType> lookup = new EnumLookup<>(OrderType.class);
    private final String code;

    OrderType(String code) {
        this.code = code;
    }

    public static OrderType lookup(String name) {
        return lookup.lookup(name);
    }

    @Override
    public String toString() {
        return code;
    }

}
