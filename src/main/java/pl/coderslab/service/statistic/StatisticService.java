package pl.coderslab.service.statistic;

import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.HistoryOrder;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.model.statistic.CommonStatInterface;
import pl.coderslab.model.statistic.ShiftTrade;
import pl.coderslab.model.statistic.SourceStat;
import pl.coderslab.model.statistic.SymbolStat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;

@Service
public class StatisticService {

    public SourceStat getSourceStatistic(List<HistoryOrder> historyOrderList, Source source, boolean openTime) {
        historyOrderList = historyOrderList.stream().filter(s->source.getSymbols().stream().map(Symbol::getName).toList().contains(s.getSymbol())).toList();
        SourceStat sourceStat = new SourceStat();
        getAccuracy(historyOrderList, sourceStat);
        sourceStat.setSymbolStat(getSymbolStat(historyOrderList));
        sourceStat.setShiftTrades(getShiftTrades(historyOrderList, openTime));
        sourceStat.setHourTrades(getHourTrades(historyOrderList, openTime, false));
        sourceStat.setDayOfWeekTrades(getHourTrades(historyOrderList, openTime, true));
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

    private List<ShiftTrade> getShiftTrades(List<HistoryOrder> historyOrderList, boolean openTime){
        List<ShiftTrade> shiftTrades = new ArrayList<>();
        shiftTrades.add(getTrade(1, historyOrderList, false, openTime, false));
        shiftTrades.add(getTrade(2, historyOrderList, false, openTime, false));
        shiftTrades.add(getTrade(3, historyOrderList, false, openTime, false));
        return shiftTrades;
    }
    private List<ShiftTrade> getHourTrades(List<HistoryOrder> historyOrderList, boolean openTime, boolean dayOfWeek){
        List<ShiftTrade> hourTrades = new ArrayList<>();
        for (int i = 0; i < 7 || (!dayOfWeek && i < 24); i++) {
            hourTrades.add(getTrade(i, historyOrderList, true, openTime, dayOfWeek));
        }
        return hourTrades;
    }

    private ShiftTrade getTrade(int shift, List<HistoryOrder> historyOrderList, boolean hour, boolean openTime, boolean dayOfWeek){
        List<HistoryOrder> historyOrders = getHistoryForShift(shift, historyOrderList, hour, openTime, dayOfWeek);
        SourceStat sourceStat = new SourceStat();
        getAccuracy(historyOrders, sourceStat);
        ShiftTrade shiftTrade = new ShiftTrade();
        shiftTrade.setShift(shift);
        shiftTrade.setCountTrade(sourceStat.getCountTrade());
        shiftTrade.setCountWin(sourceStat.getCountWin());
        shiftTrade.setAccuracy(sourceStat.getAccuracy());
        return shiftTrade;
    }

    private List<HistoryOrder> getHistoryForShift(int number, List<HistoryOrder> historyOrderList, boolean shiftHour, boolean openTime, boolean dayOfWeek){
        if(dayOfWeek){
             return getHistoryForDayOfWeek(number, historyOrderList, openTime);
        } else {
            return getHistory(number, historyOrderList, shiftHour, openTime);
        }
    }

    private List<HistoryOrder> getHistory(int number, List<HistoryOrder> historyOrderList, boolean shiftHour, boolean openTime){
        int startH;
        int stopH;
        if(!shiftHour){
            startH = getStartHour(number);
            stopH = getStopHour(number);
        } else {
            startH = number;
            stopH = number + 1;
            if(stopH >= 24){
                stopH = 0;
            }
        }
        int finalStopH = stopH;
        if(openTime){
            return historyOrderList.stream()
                    .filter(s->s.getOpenTime() != null)
                    .filter(s->((s.getOpenTime().getHour() >= startH && s.getOpenTime().getHour() < finalStopH) || startH > finalStopH))
                    .filter(s->((s.getOpenTime().getHour() >= startH || s.getOpenTime().getHour() < finalStopH) || startH < finalStopH))
                    .toList();
        } else {
            return historyOrderList.stream()
                    .filter(s->((s.getCreated().getHour() >= startH && s.getCreated().getHour() < finalStopH) || startH > finalStopH))
                    .filter(s->((s.getCreated().getHour() >= startH || s.getCreated().getHour() < finalStopH) || startH < finalStopH))
                    .toList();
        }
    }

    private List<HistoryOrder> getHistoryForDayOfWeek(int number, List<HistoryOrder> historyOrderList, boolean open){
        DayOfWeek day = getDayOfWeek(number);
            return historyOrderList.stream()
                    .filter(s -> !isNull(s.getOpenTime()) || !open)
                    .filter(s-> s.getCreated().getDayOfWeek().equals(day) || open)
                    .filter(s-> s.getOpenTime().getDayOfWeek().equals(day) || !open)
                    .toList();
    }

    private DayOfWeek getDayOfWeek(int number){
        if(number == 0) return DayOfWeek.MONDAY;
        if(number == 1) return DayOfWeek.TUESDAY;
        if(number == 2) return DayOfWeek.WEDNESDAY;
        if(number == 3) return DayOfWeek.THURSDAY;
        if(number == 4) return DayOfWeek.FRIDAY;
        if(number == 5) return DayOfWeek.SATURDAY;
        if(number == 6) return DayOfWeek.SUNDAY;
        return null;
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
