package pl.coderslab.enums;

import java.math.BigDecimal;

public enum Emoticon {
    LONG("\uD83D\uDCC8"),
    SHORT("\uD83D\uDCC9"),
    WIN("\uD83D\uDCB0"),
    LOSS("\uD83D\uDEAB"),
    OWN_CLOSED("\uD83D\uDC49"),
    AVERANGE("\uD83D\uDE91"),
    OPEN("\uD83D\uDFE2"),
    CLOSE("\uD83D\uDEAB"),
    STRATEGY_SIGNAL("〽\uFE0F"),
    ALERT("⚠\uFE0F");
    private final String label;

    Emoticon(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Emoticon getWinLoss(BigDecimal realizedPLN){
        if(realizedPLN.doubleValue() >= 0){
            return WIN;
        }
        return LOSS;
    }
}
