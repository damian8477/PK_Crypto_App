package pl.coderslab.strategy.service;
//RSI Strategy by Damian

import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.rsi.RsiStrategy;
import pl.coderslab.interfaces.BinanceBasicService;
import pl.coderslab.interfaces.OrderService;
import pl.coderslab.interfaces.SourceService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.repository.RsiStrategyRepository;
import pl.coderslab.service.bot.BotService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class Strategy110Service extends BotService {

    public final int PERIOD_RSI = 14;
    public final String SOURCE_NAME = "RSI";
    private final RsiStrategyRepository rsiStrategyRepository;
    private final IndicatorsService indicatorsService;
    private final BotService botService;
    private final SourceService sourceService;
    private final BinanceBasicService binanceBasicService;
    private final OrderService orderService;
    public List<RsiStrategy> cryptoNames = new ArrayList<>();

    public Strategy110Service(BinanceBasicService binanceBasicService, IndicatorsService indicatorsService, OrderService orderService, UserService userService, RsiStrategyRepository rsiStrategyRepository, IndicatorsService indicatorsService1, BotService botService, SourceService sourceService, BinanceBasicService binanceBasicService1, OrderService orderService1) {
        super(binanceBasicService, indicatorsService, orderService, userService);
        this.rsiStrategyRepository = rsiStrategyRepository;
        this.indicatorsService = indicatorsService1;
        this.botService = botService;
        this.sourceService = sourceService;
        this.binanceBasicService = binanceBasicService1;
        this.orderService = orderService1;
    }

    public void downloadCryptoNameList() {
        cryptoNames.clear();
        cryptoNames = rsiStrategyRepository.findAll();
    }

    private void updateCountWinLoss(Order order, String totalPercentValue) {
        if (order.getStrategy().getSource().getName() == SOURCE_NAME) {
            RsiStrategy rsiStrategy = rsiStrategyRepository.findBySymbol(order.getSymbolName());
            if (totalPercentValue.contains("-")) {
                rsiStrategyRepository.updateCountLoss(rsiStrategy.getId(), rsiStrategy.getCountLossTrade() + 1);
            } else {
                rsiStrategyRepository.updateCountWin(rsiStrategy.getId(), rsiStrategy.getCountWinTrade() + 1);
            }
        }
    }

    public void searchNewOrder(List<Order> orders) {
        int count = 0;
        if(isNull(orders)){
            orders = orderService.findByUserId(1000l);
        }
        for (RsiStrategy coin : cryptoNames) {
            count++;
            double avRsi = indicatorsService.getAvrRsi(coin.getSymbol(), 14);
            String candleStick = indicatorsService.getCandleStick(coin.getSymbol());
            String avRsiString = String.valueOf(aroundValueS("1.11", String.valueOf(avRsi)));
            rsiStrategyRepository.updateRsiFun(coin.getId(), avRsiString);
            if (avRsi < coin.getSellRsiTrigger())
                if (avRsi < coin.getLongRsiTrigger()) {
                    if (!coin.isActive() && !coin.isDown()) {
                        rsiStrategyRepository.updateDown(coin.getId(), true);
                    }
                    rsiStrategyRepository.updateActive(coin.getId(), true);
                } else {
                    if (coin.isDown()) {
                        Source source = sourceService.findByName(SOURCE_NAME);
                        botService.newOrder(SOURCE_NAME, coin.getSymbol(), "LONG", "0", "0", 1.1, 0.7, String.valueOf(avRsi), orders, source, candleStick);
                    }
                    rsiStrategyRepository.updateActive(coin.getId(), false);
                    rsiStrategyRepository.updateDown(coin.getId(), false);
                }

            if (avRsi > coin.getLongRsiTrigger()) {
                if (avRsi > coin.getSellRsiTrigger()) {
                    if (!coin.isActive() && !coin.isUp()) {
                        rsiStrategyRepository.updateUp(coin.getId(), true);
                    }
                    rsiStrategyRepository.updateActive(coin.getId(), true);
                } else {
                    if (coin.isUp()) {
                        Source source = sourceService.findByName(SOURCE_NAME);
                        botService.newOrder(SOURCE_NAME, coin.getSymbol(), "SHORT", "0", "0", 1.1, 0.7, String.valueOf(avRsi), orders, source, candleStick);
                    }
                    rsiStrategyRepository.updateActive(coin.getId(), false);
                    rsiStrategyRepository.updateUp(coin.getId(), false);
                }
            }
        }
    }

    public void checkOrderStatusBot(List<Order> activeOrdersListArg) throws IOException, TimeoutException {
        List<Order> activeOrdersList = activeOrdersListArg.stream()
                .filter(s -> (s.getSource().getName().equals(SOURCE_NAME)))
                .filter(s -> s.getUser().getId() == 1000)
                .collect(Collectors.toList());
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
                } else if (order.getPositionSide().toString().equals("SHORT")) {
                    if (lowPrice < Double.parseDouble(order.getTp()) || highPrice > Double.parseDouble(order.getSl())) {
                        killOrder(order, marketPrice);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void killOrder(Order order,  double marketPrice) throws IOException, TimeoutException {
        orderService.deleteById(order.getId());
        Double totalPercentValue = aroundValue("1.11", percentProfitBot(marketPrice, order));
        updateCountWinLoss(order, String.valueOf(totalPercentValue));
        BinanceConfirmOrder binanceConfirmOrder = getBinanceConfirmOrder(order, marketPrice, totalPercentValue); //dokonczyc
        orderService.saveHistoryOrderToDB(order.getUser(), order, binanceConfirmOrder, false);
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


}
