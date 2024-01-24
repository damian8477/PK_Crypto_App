package pl.coderslab.strategy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.entity.strategy.CCIOrder;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.interfaces.*;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.repository.CCIOrderRepository;
import pl.coderslab.service.bot.BotService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
public class Strategy111Service extends BotService implements StrategyService {
    private static final String SOURCE_NAME = "CCI";
    private final IndicatorsService indicatorsService;
    private final CCIOrderRepository cciOrderRepository;
    private final BotService botService;
    private final SourceService sourceService;
    private final BinanceBasicService binanceBasicService;
    private final OrderService orderService;
    public static List<String> symbolList = new ArrayList<>();
    private List<CCIOrder> activeOrders = new ArrayList<>();
    private Map<String, Double> waitingSymbol = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(Strategy111Service.class);


    public Strategy111Service(BinanceBasicService binanceBasicService, OrderService orderService, UserService userService, IndicatorsService indicatorsService1, BotService botService, SourceService sourceService, BinanceBasicService binanceBasicService1, OrderService orderService1, SignalService signalService, OpenService openService, CCIOrderRepository cciOrderRepository) {
        super(binanceBasicService, orderService, userService, signalService, openService);
        this.indicatorsService = indicatorsService1;
        this.botService = botService;
        this.sourceService = sourceService;
        this.binanceBasicService = binanceBasicService1;
        this.orderService = orderService1;
        this.cciOrderRepository = cciOrderRepository;
    }

    @Override
    public void searchNewOrder(List<Order> orders) {
        activeOrders = cciOrderRepository.findAll();
        List<CCIOrder> cciOrders = activeOrders.stream()
                .filter(s -> !s.isOpen() && s.isActive())
                .filter(s -> symbolList.contains(s.getSymbol()))
                        .toList();
        if (isNull(orders)) {
            orders = orderService.findByUserId(1000L);
        }
        List<Order> finalOrders = orders;
        cciOrders.forEach(order -> {
            double avRsi;// = indicatorsService.getAvrRsi(order.getSymbol(), 14);
            avRsi = indicatorsService.getRSI(15, order.getSymbol(), 14);
            if (avRsi < 50.0) {
                openProcess(order, finalOrders, avRsi);
            } else if (avRsi > 75.0) {
                order.setOpen(true);
                logger.error(LocalDateTime.now() + " delete order " + order.getSymbol() + " " + order.toString() + " " + avRsi);
                waitingSymbol.remove(order.getSymbol());
            } else {
                if(!waitingSymbol.containsKey(order.getSymbol())){
                    waitingSymbol.put(order.getSymbol(), avRsi);
                }
                if(waitingSymbol.get(order.getSymbol()) - 5.0 > avRsi){
                    openProcess(order, finalOrders, avRsi);
                }
                logger.error(LocalDateTime.now() + " " + order.getSymbol() + " waiting " + avRsi);
            }
        });
    }

    public void openProcess(CCIOrder order, List<Order> orders, double rsi){
        openOrder(order, orders);
        order.setOpen(true);
        cciOrderRepository.save(order);
        logger.error(LocalDateTime.now() + " OPEN " + order.getSymbol() + rsi);
        waitingSymbol.remove(order.getSymbol());
    }
    public void openOrder(CCIOrder order, List<Order> orders) {
        Source source = sourceService.findByName(SOURCE_NAME);
        String candleStick = indicatorsService.getCandleStick(order.getSymbol());
        order.setOpen(true);
        cciOrderRepository.save(order);
        botService.newOrder(SOURCE_NAME, order.getSymbol(), "LONG", "0", "0", 2.4, 1.23, "", orders, source, candleStick);
//        botService.newOrder(SOURCE_NAME, order.getSymbol(), "LONG", "0", "0", 3.0, 0.83, "", orders, source, candleStick);
//        botService.newOrder(SOURCE_NAME, order.getSymbol(), "SHORT", "0", "0", 1.5, 2.5, "", orders, source, candleStick);
    }

    @Override
    public void checkOrderStatusBot(List<Order> activeOrdersListArg) {
        if (isNull(activeOrdersListArg)) {
            activeOrdersListArg = orderService.findByUserId(1000L);
        }
        activeOrders = cciOrderRepository.findAllByActive(true);
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
        BinanceConfirmOrder binanceConfirmOrder = getBinanceConfirmOrder(order, marketPrice, totalPercentValue);
        boolean win = false;
        if (totalPercentValue > 0) {
            win = true;
        }
        orderService.saveHistoryOrderToDB(order.getUser(), order, binanceConfirmOrder, false, win);
        cciOrderUpdateKill(order, marketPrice);
    }

    private void cciOrderUpdateKill(Order order, double marketPrice) {
        CCIOrder cciOrder = activeOrders.stream()
                .filter(s -> s.getSymbol().equals(order.getSymbolName()))
                .findFirst().orElse(null);
        if (!isNull(cciOrder)) {
            cciOrder.setActive(false);
            cciOrder.setOpen(false);
            cciOrder.setWin(Double.parseDouble(order.getEntry()) > marketPrice);
            cciOrder.setClosePrice(marketPrice);
            cciOrder.setCloseTime(LocalDateTime.now());
            cciOrderRepository.save(cciOrder);
        }
    }

    @Override
    public void downloadSymbols(){
        Source source = sourceService.findByNameWithSymbols(SOURCE_NAME);
        if(!isNull(source)){
            symbolList = source.getSymbols().stream().map(Symbol::getName).toList();
        }
    }

    @Override
    public void updateStrategyDb() {

    }
}
