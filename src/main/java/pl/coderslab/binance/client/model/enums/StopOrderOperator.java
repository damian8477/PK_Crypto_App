package pl.coderslab.binance.client.model.enums;

public enum StopOrderOperator {

    GTE("gte", "greater than and equal (>=)"), LTE("lte", "less than and equal (<=)");

    private String operator;

    private String desc;

    StopOrderOperator(String operator, String desc) {
        this.operator = operator;
        this.desc = desc;
    }

    public static StopOrderOperator find(String operator) {
        for (StopOrderOperator op : StopOrderOperator.values()) {
            if (op.getOperator().equals(operator)) {
                return op;
            }
        }
        return null;
    }

    public String getOperator() {
        return operator;
    }

    public String getDesc() {
        return desc;
    }

}
