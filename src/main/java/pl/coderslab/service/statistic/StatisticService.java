package pl.coderslab.service.statistic;

import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.HistoryOrder;
import pl.coderslab.model.statistic.SourceStat;
import pl.coderslab.model.statistic.SymbolStat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StatisticService {

    public SourceStat getSourceStatistic(List<HistoryOrder> historyOrderList) {
        SourceStat sourceStat = new SourceStat();
        sourceStat.setAccuracy(getAccuracy(historyOrderList));
        sourceStat.setSymbolStat(getSymbolStat(historyOrderList));

        return sourceStat;
    }

    private BigDecimal getAccuracy(List<HistoryOrder> historyOrderList) {
        AtomicInteger win = new AtomicInteger();
        AtomicInteger loss = new AtomicInteger();
        historyOrderList.forEach(s -> {
            if (s.getSide().equals("LONG")) {
                if (Double.parseDouble(s.getClose()) / Double.parseDouble(s.getEntry()) - 1 > 0) {
                    win.getAndIncrement();
                } else {
                    loss.getAndIncrement();
                }
            } else {
                if (s.getSide().equals("SHORT") && Double.parseDouble(s.getEntry()) / Double.parseDouble(s.getClose()) - 1 > 0) {
                    win.getAndIncrement();
                } else {
                    loss.getAndIncrement();
                }
            }


        });
        return BigDecimal.valueOf((win.doubleValue() / (loss.doubleValue() + win.doubleValue()) * 100)).setScale(2, RoundingMode.HALF_UP);
    }

    private List<SymbolStat> getSymbolStat(List<HistoryOrder> historyOrderList) {
        List<String> symbolList = historyOrderList.stream().map(HistoryOrder::getSymbol).distinct().toList();
        List<SymbolStat> symbolStats = new ArrayList<>();
        symbolList.forEach(s -> {
            List<HistoryOrder> symbolHistory = historyOrderList.stream().filter(symbol -> symbol.getSymbol().equals(s)).toList();
            BigDecimal accuracy = getAccuracy(symbolHistory);
            SymbolStat symbolStat = new SymbolStat();
            symbolStat.setCryptoName(s);
            symbolStat.setAccuracy(accuracy);
            symbolStats.add(symbolStat);
        });
        return symbolStats;
    }
}
