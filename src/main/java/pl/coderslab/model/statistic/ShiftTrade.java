package pl.coderslab.model.statistic;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShiftTrade {
    private int shift;
    private BigDecimal accuracy;
    private int countWin;
    private int countTrade;
    private SymbolStat symbolStat;
    private BigDecimal averageAccuracy;
}
