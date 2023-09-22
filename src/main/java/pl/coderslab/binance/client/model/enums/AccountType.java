package pl.coderslab.binance.client.model.enums;


import pl.coderslab.binance.client.impl.utils.EnumLookup;

public enum AccountType {
    SPOT("spot"),
    MARGIN("margin"),
    OTC("otc"),
    POINT("point"),
    SUPER_MARGIN("super-margin"),
    MINEPOOL("minepool"),
    ETF("etf"),
    AGENCY("agency"),
    UNKNOWN("unknown");

    private static final EnumLookup<AccountType> lookup = new EnumLookup<>(AccountType.class);
    private final String code;

    AccountType(String code) {
        this.code = code;
    }

    public static AccountType lookup(String name) {
        return lookup.lookup(name);
    }

    @Override
    public String toString() {
        return code;
    }

}
