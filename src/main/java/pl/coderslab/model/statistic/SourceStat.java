package pl.coderslab.model.statistic;

import lombok.Data;
import pl.coderslab.entity.strategy.Source;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SourceStat implements CommonStatInterface{
    private Source source;
    private BigDecimal accuracy;
    private int countWin;
    private int countTrade;
    private List<SymbolStat> symbolStat;
    private List<ShiftTrade> shiftTrades;
    private List<ShiftTrade> hourTrades;
    private List<ShiftTrade> dayOfWeekTrades;
}
