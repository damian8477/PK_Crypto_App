package pl.coderslab.binance.client.model.enums;


import pl.coderslab.binance.client.impl.utils.EnumLookup;

public enum EtfSwapType {
    ETF_SWAP_IN("1"),
    ETF_SWAP_OUT("2");

    private static final EnumLookup<EtfSwapType> lookup = new EnumLookup<>(EtfSwapType.class);
    private final String code;

    EtfSwapType(String code) {
        this.code = code;
    }

    public static EtfSwapType lookup(String name) {
        return lookup.lookup(name);
    }

    @Override
    public String toString() {
        return code;
    }
}
