package pl.coderslab.binance.client.model.enums;


import pl.coderslab.binance.client.impl.utils.EnumLookup;

public enum BalanceType {
    TRADE("trade"),
    FROZEN("frozen"),
    LOAN("loan"),
    INTEREST("interest"),
    LOAN_AVAILABLE("loan-available"),
    TRANSFER_OUT_AVAILABLE("transfer-out-available");


    private static final EnumLookup<BalanceType> lookup = new EnumLookup<>(BalanceType.class);
    private final String code;

    BalanceType(String code) {
        this.code = code;
    }

    public static BalanceType lookup(String name) {
        return lookup.lookup(name);
    }

    @Override
    public String toString() {
        return code;
    }

}
