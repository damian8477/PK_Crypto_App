package pl.coderslab.binance.client.model.enums;


import pl.coderslab.binance.client.impl.utils.EnumLookup;

public enum AccountState {
    WORKING("working"),
    LOCK("lock");

    private static final EnumLookup<AccountState> lookup = new EnumLookup<>(AccountState.class);
    private final String code;

    AccountState(String code) {
        this.code = code;
    }

    public static AccountState lookup(String name) {
        return lookup.lookup(name);
    }

    @Override
    public String toString() {
        return code;
    }
}
