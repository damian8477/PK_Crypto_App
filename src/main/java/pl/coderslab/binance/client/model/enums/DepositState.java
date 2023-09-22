package pl.coderslab.binance.client.model.enums;


import pl.coderslab.binance.client.impl.utils.EnumLookup;

public enum DepositState {

    UNKNOWN("unknown"),
    CONFIRMING("confirming"),
    SAFE("safe"),
    CONFIRMED("confirmed"),
    ORPHAN("orphan");


    private static final EnumLookup<DepositState> lookup = new EnumLookup<>(DepositState.class);
    private final String code;

    DepositState(String code) {
        this.code = code;
    }

    public static DepositState lookup(String name) {
        return lookup.lookup(name);
    }

    @Override
    public String toString() {
        return code;
    }

}
