package pl.coderslab.service.statistic;

import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.HistoryOrder;
import pl.coderslab.model.statistic.CommonStatInterface;
import pl.coderslab.model.statistic.ShiftTrade;
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
        getAccuracy(historyOrderList, sourceStat);
        sourceStat.setSymbolStat(getSymbolStat(historyOrderList));
        sourceStat.setShiftTrades(getShiftTrades(historyOrderList));
        return sourceStat;
    }

    private <T extends CommonStatInterface> void getAccuracy(List<HistoryOrder> historyOrderList, T t) {
        AtomicInteger win = new AtomicInteger(0);
        AtomicInteger loss = new AtomicInteger(0);
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
        if(win.doubleValue() + loss.doubleValue() > 0.0)
        {
            t.setAccuracy(BigDecimal.valueOf((win.doubleValue() / (loss.doubleValue() + win.doubleValue()) * 100)).setScale(2, RoundingMode.HALF_UP));
        } else {
            t.setAccuracy(new BigDecimal("0.0"));
        }
        t.setCountWin(win.get());
        t.setCountTrade(win.get() + loss.get());
    }

    private List<SymbolStat> getSymbolStat(List<HistoryOrder> historyOrderList) {
        List<String> symbolList = historyOrderList.stream().map(HistoryOrder::getSymbol).distinct().toList();
        List<SymbolStat> symbolStats = new ArrayList<>();
        symbolList.forEach(s -> {
            List<HistoryOrder> symbolHistory = historyOrderList.stream().filter(symbol -> symbol.getSymbol().equals(s)).toList();
            SymbolStat symbolStat = new SymbolStat();
            getAccuracy(symbolHistory, symbolStat);
            symbolStat.setCryptoName(s);
            symbolStats.add(symbolStat);
        });
        return symbolStats;
    }

    private List<ShiftTrade> getShiftTrades(List<HistoryOrder> historyOrderList){
        List<ShiftTrade> shiftTrades = new ArrayList<>();
        shiftTrades.add(getShiftTrade(1, historyOrderList));
        shiftTrades.add(getShiftTrade(2, historyOrderList));
        shiftTrades.add(getShiftTrade(3, historyOrderList));
        return shiftTrades;
    }

    private ShiftTrade getShiftTrade(int shift, List<HistoryOrder> historyOrderList){
        List<HistoryOrder> historyOrders = getHistoryForShift(shift, historyOrderList);
        SourceStat sourceStat = new SourceStat();
        getAccuracy(historyOrders, sourceStat);
        ShiftTrade shiftTrade = new ShiftTrade();
        shiftTrade.setShift(shift);
        shiftTrade.setCountTrade(sourceStat.getCountTrade());
        shiftTrade.setCountWin(sourceStat.getCountWin());
        shiftTrade.setAccuracy(sourceStat.getAccuracy());
        return null;
    }

    private List<HistoryOrder> getHistoryForShift(int shift, List<HistoryOrder> historyOrderList){
        int startH = getStartHour(shift);
        int stopH = getStopHour(shift);

        return historyOrderList.stream()
                .filter(s->(s.getCreated().getHour() >= startH && s.getCreated().getHour() < stopH))
                .toList();
    }

    private int getStartHour(int shift){
        return switch (shift) {
            case 1 -> 6;
            case 2 -> 14;
            case 3 -> 22;
            default -> 0;
        };
    }

    private int getStopHour(int shift){
        return switch (shift) {
            case 1 -> 14;
            case 2 -> 22;
            case 3 -> 6;
            default -> 0;
        };
    }
}
