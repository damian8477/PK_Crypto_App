package pl.coderslab.strategy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.interfaces.*;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.service.bot.BotService;
import pl.coderslab.service.telegram.TelegramInfoServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
public class Strategy112Service extends BotService implements StrategyService {
    private static final String SOURCE_NAME = "MACD";
    private final IndicatorsService indicatorsService;
    private static Source source;
    private final SourceService sourceService;
    private final BinanceBasicService binanceBasicService;
    private final OrderService orderService;
    public static List<Symbol> symbolList = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(Strategy112Service.class);

    public Strategy112Service(BinanceBasicService binanceBasicService, OrderService orderService, UserService userService, SignalService signalService, OpenService openService, TelegramInfoServiceImpl telegramInfoService, IndicatorsService indicatorsService, BotService botService, SourceService sourceService, BinanceBasicService binanceBasicService1, TelegramInfoServiceImpl telegramInfoService1, OrderService orderService1) {
        super(binanceBasicService, orderService, userService, signalService, openService, telegramInfoService);
        this.indicatorsService = indicatorsService;
        this.sourceService = sourceService;
        this.binanceBasicService = binanceBasicService1;
        this.orderService = orderService1;
    }


    @Override
    public void searchNewOrder(List<Order> orders) {
        if (isNull(orders)) {
            orders = orderService.findByUserId(1000L).stream().filter(s->s.getSource().getName().equals(SOURCE_NAME)).toList();
        }
        List<Order> finalOrders = orders;
        symbolList.forEach(symbol -> {
            calculate(symbol, finalOrders);
        });
    }

    private void calculate(Symbol symbol, List<Order> orders) {
        String cryptoName = symbol.getName();

        if(orders.stream().filter(s -> s.getSymbolName().equals(symbol.getName())).toList().isEmpty() && symbol.isActive()){
            List<Double> candlestickList15m = indicatorsService.candleReturn(15, cryptoName, 300);
            List<Double> candlestickList1m = indicatorsService.candleReturn(1, cryptoName, 300);
            double markPrice = binanceBasicService.getMarketPriceDouble(null, cryptoName);
            double macd15m = indicatorsService.getMacd(15, cryptoName, 0, 12, 26, 9, candlestickList15m);
            double macdPrev15m = indicatorsService.getMacdPrev(15, cryptoName, 0, 12, 26, 9, candlestickList15m);
            double ema20015m = indicatorsService.getEMA(15, cryptoName, 200, candlestickList15m, markPrice);
            double ema5015m = indicatorsService.getEMA(15, cryptoName, 50, candlestickList15m, markPrice);
            double ema2001m = indicatorsService.getEMA(1, cryptoName, 200, candlestickList1m, markPrice);
            double ema501m = indicatorsService.getEMA(1, cryptoName, 50, candlestickList1m, markPrice);


            if (ema501m < ema2001m && ema5015m < ema20015m) {
                if (macd15m > 0 && macdPrev15m > 0) {
                    double macdPrev = macdPrev15m * 0.96;
                    if (macd15m < macdPrev) {
                        newOrder(SOURCE_NAME, cryptoName, "SHORT", "0", "0", 2.4, 1.23, "", orders, source, candlestickList1m.get(0).toString());
                    }
                }
            }
            if (ema501m > ema2001m && ema5015m > ema20015m) {
                if (macd15m < 0 && macdPrev15m < 0) {
                    double macdPrev = macdPrev15m * 1.04;
                    if (macd15m > macdPrev) {
                        newOrder(SOURCE_NAME, cryptoName, "LONG", "0", "0", 2.4, 1.23, "", orders, source, candlestickList1m.get(0).toString());
                    }
                }
            }
        }
    }

    @Override
    public void checkOrderStatusBot(List<Order> activeOrdersListArg) {
        if (isNull(activeOrdersListArg)) {
            activeOrdersListArg = orderService.findByUserId(1000L);
        }
        List<Order> activeOrdersList = activeOrdersListArg.stream()
                .filter(s -> (s.getSource().getName().equals(SOURCE_NAME)))
                .filter(s -> s.getUser().getId() == 1000)
                .toList();
        for (Order order : activeOrdersList) {
            try {
                double marketPrice = binanceBasicService.getMarketPriceDouble(null, order.getSymbolName());
                Map<String, Double> highLowMarketPrice = indicatorsService.candle1mLowHigh(order.getSymbolName());
                double lowPrice = highLowMarketPrice.get("Low");
                double highPrice = highLowMarketPrice.get("High");

                if (order.getPositionSide().toString().equals("LONG")) {
                    if (highPrice > Double.parseDouble(order.getTp()) || lowPrice < Double.parseDouble(order.getSl())) {
                        killOrder(order, marketPrice);
                    }
                } else if (order.getPositionSide().toString().equals("SHORT") && (lowPrice < Double.parseDouble(order.getTp()) || highPrice > Double.parseDouble(order.getSl()))) {
                    killOrder(order, marketPrice);
                }
            } catch (Exception e) {
                logger.info(getStringFormat("%s %s", "checkOrderStatusBot ", e.getMessage()));
            }
        }
    }

    @Override
    public void killOrder(Order order, double marketPrice) {
        orderService.deleteById(order.getId());
        double totalPercentValue = aroundValue("1.11", percentProfitBot(marketPrice, order));
        BinanceConfirmOrder binanceConfirmOrder = getBinanceConfirmOrder(order, marketPrice, totalPercentValue);
        boolean win = totalPercentValue > 0;
        orderService.saveHistoryOrderToDB(order.getUser(), order, binanceConfirmOrder, false, win);
        sendInfoBotTelegram(getStringFormat("%s %s %s %s%", SOURCE_NAME, order.getSymbolName(), order.getPositionSide().toString(), totalPercentValue));
    }

    @Override
    public void downloadSymbols(){
        source = sourceService.findByNameWithSymbols(SOURCE_NAME);
        if(!isNull(source)){
            symbolList = source.getSymbols();
        }
    }

    @Override
    public void updateStrategyDb() {

    }
}
