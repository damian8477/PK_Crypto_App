package pl.coderslab.service.bot;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.common.Common;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.orders.Signal;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.user.User;
import pl.coderslab.interfaces.BinanceBasicService;
import pl.coderslab.interfaces.OrderService;
import pl.coderslab.interfaces.SignalService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.strategy.service.IndicatorsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BotService extends Common {
    private final BinanceBasicService binanceBasicService;
    private final IndicatorsService indicatorsService;
    private final OrderService orderService;
    private final UserService userService;
    private final SignalService signalService;

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

                if (percentSl > 0.0) stopPercent = percentSl * 0.01;
                if (percentTp > 0.0) takePercent = (percentTp * 0.01);

                if (side.equals("LONG")) {
                    stopLoss = aroundValue(candleStick, (1.0 - stopPercent) * marketPrice);
                    takeProfit = aroundValue(candleStick, (1.0 + takePercent) * marketPrice);
                } else {
                    stopLoss = aroundValue(candleStick, (1.0 + stopPercent) * marketPrice);
                    takeProfit = aroundValue(candleStick, (1.0 - takePercent) * marketPrice);
                }
                if (!tp.equals("0"))
                    takeProfit = aroundValue(candleStick, Double.parseDouble(tp));
                if (!sl.equals("0"))
                    stopLoss = aroundValue(candleStick, Double.parseDouble(sl));

                if (rsi.equals("")) {
                    rsi = String.valueOf(indicatorsService.getAvrRsi(symbol, 14));
                }
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
                }
            }
        } catch (Exception e) {
            logger.info(symbol + " " + getTimeStamp() + "error in newOrder" + e);
        }
    }

    public void checkOrderStatusBot(String sourceName, List<Order> activeOrdersListArg) {
        List<Order> activeOrdersList = activeOrdersListArg.stream()
                .filter(s -> (s.getSource().getName().equals(sourceName)))
                .filter(s -> s.getUser().getId() == 1000)
                .toList();
        for (Order activeOrders : activeOrdersList) {
            double marketPrice = binanceBasicService.getMarketPriceDouble(null, activeOrders.getSymbolName());
            normalBot(activeOrders, marketPrice);
        }
    }

    public void normalBot(Order order, double marketPrice) {
        Map<String, Double> highLowMarketPrice = indicatorsService.candle1mLowHigh(order.getSymbolName());
        double lowPrice = highLowMarketPrice.get("Low");
        double highPrice = highLowMarketPrice.get("High");
        if (order.getPositionSide().toString().equals("LONG")) {
            if (highPrice > Double.parseDouble(order.getTp())) {
                killOrder(order, Double.parseDouble(order.getTp()));
            } else if (lowPrice < Double.parseDouble(order.getSl())) {
                killOrder(order, Double.parseDouble(order.getSl()));
            }
        } else if (order.getPositionSide().toString().equals("SHORT")) {
            if (lowPrice < Double.parseDouble(order.getTp())) {
                killOrder(order, Double.parseDouble(order.getTp()));
            } else if (highPrice > Double.parseDouble(order.getSl())) {
                killOrder(order, Double.parseDouble(order.getSl()));
            }
        }
    }

    public void killOrder(pl.coderslab.entity.orders.Order order, double marketPrice) {
        orderService.deleteById(order.getId());
        Double totalPercentValue = aroundValue("1.11", percentProfitBot(marketPrice, order));

    }

    public double percentProfitBot(double markPrice, Order order) {
        int leverange = 10;
        if (order.getLeverage() > 0) {
            leverange = order.getLeverage();
        }
        double entryPrice = Double.parseDouble(order.getEntry());
        if (order.getPositionSide().toString().equals("LONG")) {
            return (markPrice - entryPrice) / entryPrice * 100.0 * (double) leverange;
        } else {
            return (entryPrice - markPrice) / entryPrice * 100.0 * (double) leverange;
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