package pl.coderslab.service.telegram.vip;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.interfaces.BinanceBasicService;
import pl.coderslab.interfaces.OpenService;
import pl.coderslab.interfaces.SignalService;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.service.parser.VipSignalParserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestSignalVipServiceImpl {
    private final VipSignalParserService vipSignalParserService;
    private final SignalService signalService;
    private final OpenService openService;
    private final BinanceBasicService binanceBasicService;

    private static final Logger logger = LoggerFactory.getLogger(RequestSignalVipServiceImpl.class);

    public void newMessage(String message){
        if(message.contains("BUY") || message.contains("SELL")){
            CommonSignal signal = vipSignalParserService.parseSignalMessage(message);
            //signal = changeSignal(signal);
            logger.info("Signal Vip: " + signal);
            signal.setSignal(signalService.saveSignalFromCommonSignal(signal));
            openService.newSignal(signal);

        }
    }

    private CommonSignal changeSignal(CommonSignal signal){
        if(signal.getPositionSide().toString().equals("LONG")){
            Double marketPrice = binanceBasicService.getMarketPriceDouble(null, signal.getSymbol());
            signal.setPositionSide(PositionSide.SHORT);
            signal.setOrderType(OrderType.MARKET);
            signal.setStopLoss(getStopLoss(signal));
            signal.setTakeProfit(getTakeProfits(signal, marketPrice));
            signal.setEntryPrice(getEntryPrice(signal));


        }
        return signal;
    }

    private List<BigDecimal> getTakeProfits(CommonSignal signal, Double marketPrice){
        BigDecimal tp = signal.getTakeProfit().get(2);
        List<BigDecimal> takeProfit = new ArrayList<>();
        double percent = 0.75;
        for (int i = 1; i <= 5; i++){
            takeProfit.add(BigDecimal.valueOf(aroundValue(String.valueOf(tp), marketPrice * ((100.0 - (percent * i)) / 100))));
        }
        return takeProfit;
    }

    private List<BigDecimal> getStopLoss(CommonSignal signal){
        return List.of(signal.getTakeProfit().get(2));
    }
    private List<BigDecimal> getEntryPrice(CommonSignal signal){
        return List.of(signal.getEntryPrice().get(0));
    }

    private double aroundValue(String enterPrice, double div) {
        String[] result = enterPrice.split("\\.");
        String part2 = result[1];
        double precision;
        if (!part2.isEmpty()) {
            precision = Math.pow(10.0, part2.length());
        } else {
            precision = 10.0;
        }
        div = Math.round(div * precision);
        div = div / precision;
        return div;
    }



}
