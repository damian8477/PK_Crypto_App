package pl.coderslab.strategy.indicators.cci;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.CCIIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.CandlestickInterval;
import pl.coderslab.binance.client.model.market.Candlestick;
import pl.coderslab.entity.strategy.CCIOrder;
import pl.coderslab.entity.strategy.rsi.RsiStrategy;
import pl.coderslab.interfaces.SyncService;
import pl.coderslab.repository.CCIOrderRepository;
import pl.coderslab.repository.RsiStrategyRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CCIStrategy {
    private final SyncService syncService;
    private final RsiStrategyRepository rsiStrategyRepository;
    private final CCIOrderRepository cciOrderRepository;
    private static final Logger logger = LoggerFactory.getLogger(CCIStrategy.class);
    private int countWin = 0;
    private int countLoss = 0;
    private int countActive = 0;
    private final double SL = 0.05;
    private final double TP = 0.009;
    private static  Map<String, Position> currentPosition;
    List<Candlestick> list;


    public void searchCCI(){
        currentPosition = new HashMap<>();
        SyncRequestClient syncRequestClient = syncService.sync(null);
        CandlestickInterval interval = CandlestickInterval.FIFTEEN_MINUTES;
        int limit = 1000;
        List<String> symbols = rsiStrategyRepository.findAll().stream()
                        .map(RsiStrategy::getSymbol).toList();
        symbols.forEach(s-> cci(s, interval, limit, syncRequestClient));
        checkCurrentPosition();
    }

    private void cci(String cryptoName, CandlestickInterval interval, int limit, SyncRequestClient syncRequestClient){
        list = syncRequestClient.getCandlestick(cryptoName, interval, null, null, limit);
        BarSeries series = new BaseBarSeries();
        buildSeries(series, list);
        Strategy strategy = buildStrategy(series);
        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord = seriesManager.run(strategy);
        ZonedDateTime time = !isNull(tradingRecord.getCurrentPosition().getEntry()) ? series.getBar(tradingRecord.getCurrentPosition().getEntry().getIndex()).getEndTime() : null;
//        String info = String.format("%n%nSymbol: %s co: %s %n curPos: %s %s%n LastTrade: %s%n LastEntry: %s%n LastExit: %s%n LastPos: %s%n Positions: %s", cryptoName,
//        tradingRecord.getPositionCount(),
//        tradingRecord.getCurrentPosition(),
//        time,
//        tradingRecord.getLastTrade(),
//        tradingRecord.getLastEntry(),
//        tradingRecord.getLastExit(),
//        tradingRecord.getLastPosition(),
//        tradingRecord.getPositions());
        //logger.info(info);
        tradingRecord.getPositions().forEach(position->{
            checkPosition(position, list);
            //logger.info(position.toString());
        });
        if(tradingRecord.getCurrentPosition().getEntry() != null){
            currentPosition.put(cryptoName, tradingRecord.getCurrentPosition());
        }

    }

    private Strategy buildStrategy(BarSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        CCIIndicator longCci = new CCIIndicator(series, 50);
        CCIIndicator shortCci = new CCIIndicator(series, 5);
        Num plus100 = series.numOf(100);
        Num minus100 = series.numOf(-100);

        Rule entryRule = new OverIndicatorRule(longCci, plus100) // Bull trend
                .and(new UnderIndicatorRule(shortCci, minus100)); // Signal

        Rule exitRule = new UnderIndicatorRule(longCci, minus100) // Bear trend
                .and(new OverIndicatorRule(shortCci, plus100)); // Signal

        Strategy strategy = new BaseStrategy(entryRule, exitRule);
        return strategy;
    }

    private void buildSeries(BarSeries series, List<Candlestick> candlestickList) {
        candlestickList.forEach(s->{
            series.addBar(ZonedDateTime.ofInstant(Instant.ofEpochMilli(s.getCloseTime()), ZoneId.systemDefault()), s.getClose(), s.getOpen(),  s.getLow(), s.getHigh(), s.getVolume());
        });
    }

    private void checkPosition(Position position, List<Candlestick> candlestickList){
        int index = position.getEntry().getIndex();
        double entryPrice = position.getEntry().getNetPrice().doubleValue();
        List<BigDecimal> percents = new ArrayList<>();
        List<Integer> countSL = new ArrayList<>();
        List<Integer> countTP = new ArrayList<>();
        boolean active = true;
        for (int i = index; i < candlestickList.size(); i++) {
            double high = candlestickList.get(i).getHigh().doubleValue();
            double percent = 1 + (high - entryPrice)/entryPrice;
            if(percent > (1 + TP)){
                countTP.add(i);
                countWin++;
                active = false;
                break;
            }
            if(percent < (1 - SL)){
                countSL.add(i);
                countLoss++;
                active = false;
                break;
            }
            percents.add(BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP));
        }
        if(active){
            countActive++;
        }
//        logger.info("Price " + candlestickList.get(candlestickList.size()-1).getClose());
//        logger.info("Percent " + percents);
//        logger.info("TP " + countTP);
//        logger.info("SL " + countSL);
//        logger.info("cWin " + countWin);
//        logger.info("cLoss " + countLoss);
//        logger.info("cAct " + countActive);
    }

    private void checkCurrentPosition(){
        List<CCIOrder> activeOrders = cciOrderRepository.findAllByActive(true);
        currentPosition.forEach((symbol, position) -> {
            boolean newOrder = cciOrderRepository.findAllByOpenTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(list.get(position.getEntry().getIndex()).getCloseTime()), ZoneId.systemDefault())).stream().filter(s->s.getSymbol().equals(symbol)).toList().isEmpty();
            if(activeOrders.stream().noneMatch(s -> s.getSymbol().equals(symbol)) && newOrder){
                CCIOrder cciOrder = CCIOrder.builder()
                        .symbol(symbol)
                        .openTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(list.get(position.getEntry().getIndex()).getCloseTime()), ZoneId.systemDefault()))
                        .openPrice(position.getEntry().getNetPrice().doubleValue())
                        .active(true)
                        .build();
                cciOrderRepository.save(cciOrder);
            }
        });
    }
}
