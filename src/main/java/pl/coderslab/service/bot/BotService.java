package pl.coderslab.service.bot;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.common.Common;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.orders.Signal;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.user.User;
import pl.coderslab.interfaces.*;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.model.CommonSignal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BotService implements Common {
    private final BinanceBasicService binanceBasicService;
    private final OrderService orderService;
    private final UserService userService;
    private final SignalService signalService;
    private final OpenService openService;

    private static final Logger logger = LoggerFactory.getLogger(BotService.class);

    public void newOrder(String sourceName, String symbol, String side, String tp, String sl, double percentSl, double percentTp, String rsi, List<Order> orders, Source source, String candleStick) {
        try {
            User bot = userService.getUserBasic("bot");
            List<Order> activeOrdersList = orders.stream()
                    .filter(s -> s.getSource().getName().equals(sourceName))
                    .filter(s -> s.getSymbolName().equals(symbol))
                    .filter(s -> s.getUser().getId() == 1000)
                    .toList();
            if (activeOrdersList.isEmpty()) {
                double stopPercent = 3.0;
                double takePercent = 1.0;
                double marketPrice = binanceBasicService.getMarketPriceDouble(null, symbol);
                double stopLoss;
                double takeProfit;
                double entryPriceSecond;
                double entryPriceThird;


                if (percentSl > 0.0) stopPercent = percentSl * 0.01;
                if (percentTp > 0.0) takePercent = (percentTp * 0.01);

                if (side.equals("LONG")) {
                    stopLoss = aroundValue(candleStick, (1.0 - stopPercent) * marketPrice);
                    takeProfit = aroundValue(candleStick, (1.0 + takePercent) * marketPrice);
                    entryPriceSecond = aroundValue(candleStick, (0.99 * marketPrice));
                    entryPriceThird = aroundValue(candleStick, (0.98 * marketPrice));
                } else {
                    stopLoss = aroundValue(candleStick, (1.0 + stopPercent) * marketPrice);
                    entryPriceSecond = aroundValue(candleStick, (1.01 * marketPrice));
                    entryPriceThird = aroundValue(candleStick, (1.02 * marketPrice));
                    takeProfit = aroundValue(candleStick, (1.0 - takePercent) * marketPrice);
                }
                if (!tp.equals("0"))
                    takeProfit = aroundValue(candleStick, Double.parseDouble(tp));
                if (!sl.equals("0"))
                    stopLoss = aroundValue(candleStick, Double.parseDouble(sl));

                if ((side.equals("LONG") && stopLoss < marketPrice) || (side.equals("SHORT") && stopLoss > marketPrice)) {
                    Signal signal = Signal.builder()
                            .symbol(symbol)
                            .positionSide(PositionSide.valueOf(side))
                            .entryPrice(BigDecimal.valueOf(marketPrice))
                            .takeProfit1(BigDecimal.valueOf(takeProfit))
                            .stopLoss(BigDecimal.valueOf(stopLoss))
                            .source(source)
                            .build();
                    signalService.save(signal);
                    orderService.save(bot, signal, "", "10.0", null, 10, null, true, source, true);
                    openService.newSignal(CommonSignal.builder()
                                    .orderType(OrderType.MARKET)
                                    .isStrategy(true)
                                    .positionSide(signal.getPositionSide())
                                    .lever(10)
                                    .signal(signal)
                                    .symbol(signal.getSymbol())
                                    .sourceName(signal.getSource().getName())
                                    .entryPrice(List.of(BigDecimal.valueOf(marketPrice), BigDecimal.valueOf(entryPriceSecond), BigDecimal.valueOf(entryPriceThird)))
                                    .takeProfit(List.of(signal.getTakeProfit1()))
                                    .stopLoss(List.of(signal.getStopLoss()))
                            .build());
                }
            }
        } catch (Exception e) {
            logger.info(symbol + " " + getTimeStamp() + "error in newOrder" + e);
        }
    }

    public void killOrder(pl.coderslab.entity.orders.Order order, double marketPrice) {
        orderService.deleteById(order.getId());
    }

    public double percentProfitBot(double markPrice, Order order) {
        int leverange = 10;
        if (order.getLeverage() > 0) {
            leverange = order.getLeverage();
        }
        double entryPrice = Double.parseDouble(order.getEntry());
        if (order.getPositionSide().equals(PositionSide.LONG)) {
            return (markPrice - entryPrice) / entryPrice * 100.0 * leverange;
        } else {
            return (entryPrice - markPrice) / entryPrice * 100.0 *  leverange;
        }
    }

    public BinanceConfirmOrder getBinanceConfirmOrder(pl.coderslab.entity.orders.Order order, double marketPrice, double profit) {
        String symbol = order.getSymbolName();
        double commission = 0.2;
        return BinanceConfirmOrder.builder()
                .symbol(symbol)
                .realizedPln(BigDecimal.valueOf(profit).setScale(2, RoundingMode.HALF_UP))
                .commission(BigDecimal.valueOf(commission).setScale(2, RoundingMode.HALF_UP))
                .time(getTimeStamp())
                .closePrice(String.valueOf(marketPrice))
                .entryPrice(order.getEntry())
                .lot(order.getLot())
                .build();
    }


}