package pl.coderslab.strategy.indicators.cci;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.CCIIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.CandlestickInterval;
import pl.coderslab.binance.client.model.market.Candlestick;
import pl.coderslab.interfaces.SyncService;
import pl.coderslab.service.telegram.TelegramBotServiceImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CCIStrategy {
    private final SyncService syncService;
    private static final Logger logger = LoggerFactory.getLogger(CCIStrategy.class);

    public void searchCCI(){
        SyncRequestClient syncRequestClient = syncService.sync(null);
        CandlestickInterval interval = CandlestickInterval.FIVE_MINUTES;
        int limit = 1000;
        List<String> symbols = List.of("BTCUSDT", "ETHUSDT", "XRPUSDT", "TRBUSDT", "CELOUSDT");
        symbols.forEach(s-> cci(s, interval, limit, syncRequestClient));
    }

    private void cci(String cryptoName, CandlestickInterval interval, int limit, SyncRequestClient syncRequestClient){
        List<Candlestick> list = syncRequestClient.getCandlestick(cryptoName, interval, null, null, limit);
        BarSeries series = new BaseBarSeries();
        buildSeries(series, list);
        Strategy strategy = buildStrategy(series);
        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord = seriesManager.run(strategy);
        String info = String.format("%n%nSymbol: %s co: %s %n curPos: %s%n LastTrade: %s%n LastEntry: %s%n LastExit: %s%n LastPos: %s%n", cryptoName,
        tradingRecord.getPositionCount(),
        tradingRecord.getCurrentPosition(),
        tradingRecord.getLastTrade(),
        tradingRecord.getLastEntry(),
        tradingRecord.getLastExit(),
        tradingRecord.getLastPosition());
        logger.info(info);
    }

    private Strategy buildStrategy(BarSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }
        CCIIndicator longCci = new CCIIndicator(series, 200);
        CCIIndicator shortCci = new CCIIndicator(series, 5);
        Num plus100 = series.numOf(100);
        Num minus100 = series.numOf(-100);

        Rule entryRule = new OverIndicatorRule(longCci, plus100) // Bull trend
                .and(new UnderIndicatorRule(shortCci, minus100)); // Signal

        Rule exitRule = new UnderIndicatorRule(longCci, minus100) // Bear trend
                .and(new OverIndicatorRule(shortCci, plus100)); // Signal

        Strategy strategy = new BaseStrategy(entryRule, exitRule);
        //strategy.setUnstableBars(5);
        return strategy;
    }

    private void buildSeries(BarSeries series, List<Candlestick> candlestickList) {
        candlestickList.forEach(s->{
            series.addBar(ZonedDateTime.ofInstant(Instant.ofEpochMilli(s.getCloseTime()), ZoneId.systemDefault()), s.getClose(), s.getOpen(),  s.getLow(), s.getHigh(), s.getVolume());
        });
    }
}
