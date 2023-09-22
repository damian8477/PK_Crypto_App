package pl.coderslab.binance.client.model.enums;

import pl.coderslab.binance.client.impl.utils.EnumLookup;


public enum AccountChangeType {


    NEWORDER("order.place"),

    TRADE("order.match"),

    REFUND("order.refund"),

    CANCELORDER("order.cancel"),

    FEE("order.fee-refund"),

    TRANSFER("margin.transfer"),

    LOAN("margin.loan"),

    INTEREST("margin.interest"),

    REPAY("margin.repay"),

    OTHER("other"),

    INVALID("INVALID");

    private static final EnumLookup<AccountChangeType> lookup = new EnumLookup<>(
            AccountChangeType.class);
    private final String code;

    AccountChangeType(String code) {
        this.code = code;
    }

    public static AccountChangeType lookup(String name) {
        return lookup.lookup(name);
    }

    @Override
    public String toString() {
        return code;
    }


}
