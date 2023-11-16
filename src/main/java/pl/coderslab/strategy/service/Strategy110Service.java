package pl.coderslab.strategy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.rsi.RsiStrategy;
import pl.coderslab.interfaces.*;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.repository.RsiStrategyRepository;
import pl.coderslab.service.bot.BotService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
public class Strategy110Service extends BotService {
    private static final String SOURCE_NAME = "RSI";
    private final RsiStrategyRepository rsiStrategyRepository;
    private final IndicatorsService indicatorsService;
    private final BotService botService;
    private final SourceService sourceService;
    private final BinanceBasicService binanceBasicService;
    private final OrderService orderService;
    private List<RsiStrategy> cryptoNames = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(Strategy110Service.class);


    public Strategy110Service(BinanceBasicService binanceBasicService, IndicatorsService indicatorsService, OrderService orderService, UserService userService, RsiStrategyRepository rsiStrategyRepository, IndicatorsService indicatorsService1, BotService botService, SourceService sourceService, BinanceBasicService binanceBasicService1, OrderService orderService1, SignalService signalService) {
        super(binanceBasicService, indicatorsService, orderService, userService, signalService);
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
        if (order.getSource().getName().equals(SOURCE_NAME)) {
            RsiStrategy rsiStrategy = rsiStrategyRepository.findBySymbol(order.getSymbolName());
            if (totalPercentValue.contains("-")) {
                rsiStrategyRepository.updateCountLoss(rsiStrategy.getId(), rsiStrategy.getCountLossTrade() + 1);
            } else {
                rsiStrategyRepository.updateCountWin(rsiStrategy.getId(), rsiStrategy.getCountWinTrade() + 1);
            }
        }
    }

    public void searchNewOrder(List<Order> orders) {
        downloadCryptoNameList();
        if (isNull(orders)) {
            orders = orderService.findByUserId(1000L);
        }
        for (RsiStrategy coin : cryptoNames) {
            double avRsi = indicatorsService.getAvrRsi(coin.getSymbol(), 14);
            String candleStick = indicatorsService.getCandleStick(coin.getSymbol());
            String avRsiString = String.valueOf(aroundValueS("1.11", String.valueOf(avRsi)));
            rsiStrategyRepository.updateRsiFun(coin.getId(), avRsiString);
            if (avRsi < coin.getSellRsiTrigger()) {
                searchLong(avRsi, coin, orders, candleStick);
            }
            if (avRsi > coin.getLongRsiTrigger()) {
                searchShort(avRsi, coin, orders, candleStick);
            }
        }
    }

    private void searchLong(double avRsi, RsiStrategy coin, List<Order> orders, String candleStick) {
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
    }

    private void searchShort(double avRsi, RsiStrategy coin, List<Order> orders, String candleStick) {
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
        Double totalPercentValue = aroundValue("1.11", percentProfitBot(marketPrice, order));
        updateCountWinLoss(order, String.valueOf(totalPercentValue));
        BinanceConfirmOrder binanceConfirmOrder = getBinanceConfirmOrder(order, marketPrice, totalPercentValue); //dokonczyc
        orderService.saveHistoryOrderToDB(order.getUser(), order, binanceConfirmOrder, false);
    }

    public void checkCoinInStrategy(){
        List<String> symbolOfBinance = binanceBasicService.getSymbolList();
        List<RsiStrategy> rsiSymbols = rsiStrategyRepository.findAll();
        rsiSymbols.forEach(s->{
            if(!symbolOfBinance.contains(s.getSymbol())){
                rsiStrategyRepository.delete(s);
            }
        });
    }


}
