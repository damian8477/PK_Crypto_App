package pl.coderslab.model.statistic;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SymbolStat implements CommonStatInterface{
    private String cryptoName;
    private BigDecimal accuracy;
    private int countWin;
    private int countTrade;
    private BigDecimal percent;
}
