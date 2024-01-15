package pl.coderslab.model.statistic;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SymbolStat {
    private String cryptoName;
    private BigDecimal accuracy;
    private Integer countWin;
    private Integer countTrade;
}
