package pl.coderslab.service.scheduled;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.trade.Order;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.binance.common.Common;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.Emoticon;
import pl.coderslab.interfaces.*;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CheckUserOrderServiceImpl implements CheckUserOrderService, Common {
    private final SyncService syncService;
    private final BinanceService binanceService;
    private final TelegramBotService telegramBotService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserSettingService userSettingService;
    private static final Logger logger = LoggerFactory.getLogger(CheckUserOrderServiceImpl.class);

    @Override
    public void checkInActiveOrder(List<User> users) {
        users.forEach(user -> {
            if (!user.getOrders().isEmpty() && userSettingService.userSettingExist(user.getUserSetting())) {
                SyncRequestClient syncRequestClient = syncService.sync(user.getUserSetting().get(0));
                List<PositionRisk> positions = syncRequestClient.getPositionRisk();
                updateSlAndTp(syncRequestClient, user, positions);
                checkOpenOrders(syncRequestClient, user, positions);
                checkWaitingOrders(syncRequestClient, user, positions);
            }
        });
    }

    public void checkOpenOrders(SyncRequestClient syncRequestClient, User user, List<PositionRisk> openedPositions) {
        user.getOrders().stream()
                .filter(pl.coderslab.entity.orders.Order::isOpen)
                .forEach(order -> {
                    PositionRisk position = openedPositions.stream()
                            .filter(s -> s.getSymbol().equals(order.getSymbolName()))
                            .filter(s -> s.getPositionSide().equals(order.getPositionSide().toString()))
                            .findFirst().orElse(null);
                    double marketPrice = position.getMarkPrice().doubleValue();
                    if (position.getPositionAmt().doubleValue() == 0.0) {
                        if (!order.isStrategy()) {
                            closeOrderSignal(syncRequestClient, order, marketPrice, user, "Zlecenie zamknięte samodzielnie! ", Emoticon.OWN_CLOSED.getLabel(), true);
                        } else {
                            closeOrderSignal(syncRequestClient, order, marketPrice, user, "Zlecenie zamknięte! ", Emoticon.CLOSE.getLabel(), false);
                        }
                    } else {
                        checkStopLoss(syncRequestClient, user, position, order);
                    }
                });
    }

    public void closeOrderSignal(SyncRequestClient syncRequestClient, pl.coderslab.entity.orders.Order order, double marketPrice, User user, String mess, String emoticon, boolean ownClosed) {
        cancelAllOpenOrders(syncRequestClient, order.getSymbolName(), order.getPositionSide(), null, false);
        orderRepository.deleteById(order.getId());
        BinanceConfirmOrder binanceConfirmOrder = binanceService.getBinanceConfirmOrder(syncRequestClient, order, marketPrice);
        binanceConfirmOrder.setEntryPrice(order.getEntry());
        binanceConfirmOrder.setLot(order.getLot());
        String message = String.format(mess + " %s $%s %s Lot: %s %s", emoticon, order.getSymbolName(), order.getPositionSide(), order.getLot(), binanceConfirmOrder.getRealizedPln());
        boolean win = false;
        if (binanceConfirmOrder.getRealizedPln().compareTo(new BigDecimal("0.0")) > 0) {
            win = true;
        }
        orderService.saveHistoryOrderToDB(user, order, binanceConfirmOrder, ownClosed, win);
        telegramBotService.sendMessage(user.getUserSetting().get(0).getTelegramChatId(), message);
    }

    public void checkStopLoss(SyncRequestClient sync, User user, PositionRisk position, pl.coderslab.entity.orders.Order order) {
        List<Order> orders = sync.getOpenOrders(position.getSymbol());
        String orderType = OrderType.STOP_MARKET.toString();
        double stopLossLot = orders.stream()
                .filter(s -> s.getType().equals(orderType))
                .filter(s -> s.getPositionSide().equals(order.getPositionSide().toString()))
                .map(Order::getOrigQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
        if (position.getPositionAmt().abs().doubleValue() != stopLossLot) {
            cancelAllOpenOrders(sync, position.getSymbol(), order.getPositionSide(), orderType, true);
            binanceService.sendSlToAccount(sync, order.getSymbolName(), order.getPositionSide(), order.getSl(), position.getPositionAmt().abs().toString());
        }
    }

    public void checkWaitingOrders(SyncRequestClient syncRequestClient, User user, List<PositionRisk> positionRisks) {
        user.getOrders().stream()
                .filter(s -> !s.isOpen())
                .forEach(order -> {
                    PositionRisk position = positionRisks.stream()
                            .filter(s -> s.getSymbol().equals(order.getSymbolName()))
                            .filter(s -> s.getPositionSide().equals(order.getPositionSide().toString()))
                            .filter(s -> s.getPositionAmt().doubleValue() != 0.0)
                            .findFirst().orElse(null);
                    List<Order> orders = syncRequestClient.getOpenOrders(order.getSymbolName());
                    double marketPrice = positionRisks.stream()
                            .filter(s -> s.getSymbol().equals(order.getSymbolName()))
                            .findFirst()
                            .map(PositionRisk::getMarkPrice)
                            .map(BigDecimal::doubleValue)
                            .orElse(0.0);
                    if (!isNull(position)) {
                        List<Order> takeProfitOrder = orders.stream()
                                .filter(s -> s.getType().equals(OrderType.TAKE_PROFIT_MARKET.toString()))
                                .filter(s -> s.getPositionSide().equals(order.getPositionSide().toString()))
                                .toList();
                        List<Order> stopLossOrder = orders.stream()
                                .filter(s -> s.getType().equals(OrderType.STOP_MARKET.toString()))
                                .filter(s -> s.getPositionSide().equals(order.getPositionSide().toString()))
                                .toList();
                        if (!takeProfitOrder.isEmpty() && stopLossOrder.isEmpty() && (binanceService.sendSlToAccount(syncRequestClient, order.getSymbolName(), order.getPositionSide(), order.getSl(), position.getPositionAmt().abs().toString()))) {
                            order.setOpen(true);
                            orderService.update(order);
                        }
                    } else {
                        if (orders.isEmpty() || (order.getPositionSide().toString().equals("LONG") && marketPrice < Double.parseDouble(order.getSl())) || (order.getPositionSide().toString().equals("SHORT") && marketPrice > Double.parseDouble(order.getSl()))) {
                            closeOrderSignal(syncRequestClient, order, marketPrice, user, "Zlecenie zamknięte! ", Emoticon.CLOSE.getLabel(), false);
                        }
                    }
                });
    }

    public void updateSlAndTp(SyncRequestClient syncRequestClient, User user, List<PositionRisk> positionRisks) {
        user.getOrders().stream()
                .filter(pl.coderslab.entity.orders.Order::isOpen)
                .forEach(order -> {
                    PositionRisk position = positionRisks.stream()
                            .filter(s -> s.getSymbol().equals(order.getSymbolName()))
                            .filter(s -> s.getPositionSide().equals(order.getPositionSide().toString()))
                            .filter(s -> s.getPositionAmt().doubleValue() != 0.0)
                            .findFirst().orElse(null);
                    if (!isNull(position)) {
                        List<Order> orders = syncRequestClient.getOpenOrders(position.getSymbol());
                        List<Order> takeProfitOrder = orders.stream()
                                .filter(s -> s.getType().equals(OrderType.TAKE_PROFIT_MARKET.toString()))
                                .filter(s -> s.getPositionSide().equals(order.getPositionSide().toString()))
                                .toList();
                        double sumLot = takeProfitOrder.stream()
                                .map(Order::getOrigQty)
                                .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
                        double positionAmount = position.getPositionAmt().doubleValue();
                        if (positionAmount > sumLot) {
                            cancelAllOpenOrders(syncRequestClient, order.getSymbolName(), order.getPositionSide(), null, true);
                            String tp = getTp(position, order);
                            binanceService.sendSlAndTpToAccount(syncRequestClient, order.getSymbolName(), null, order.getPositionSide(), order.getSl(), tp);
                        }
                    } else {
                        cancelAllOpenOrders(syncRequestClient, order.getSymbolName(), order.getPositionSide(), null, true);
                    }
                });
    }

    private String getTp(PositionRisk position, pl.coderslab.entity.orders.Order order) {
        double percentTp = getPercentTp(order);
        double newTp = aroundValue(order.getTp(), (1.0 + percentTp) * position.getMarkPrice().doubleValue());
        if ((newTp < position.getEntryPrice().doubleValue() && position.getPositionSide().equals("LONG")) || newTp > position.getEntryPrice().doubleValue() && position.getPositionSide().equals("SHORT")) {
            return String.valueOf(aroundValue(order.getTp(), position.getEntryPrice().doubleValue()));
        } else {
            return String.valueOf(aroundValue(order.getTp(), (1.0 + percentTp) * position.getMarkPrice().doubleValue()));
        }
    }

    private double getPercentTp(pl.coderslab.entity.orders.Order order) {
        return Math.abs(1 - Double.parseDouble(order.getTp()) / Double.parseDouble(order.getEntry()));
    }


    @Override
    public void cancelAllOpenOrders(SyncRequestClient syncRequestClient, String symbol, PositionSide side, String type, boolean limitOrder) {
        try {
            List<Order> orders = syncRequestClient.getOpenOrders(symbol);
            List<Order> listOrder = orders.stream()
                    .filter(s -> s.getPositionSide().equals(side.toString()))
                    .toList();
            if (limitOrder) {
                listOrder = listOrder.stream()
                        .filter(s -> !s.getType().equals("LIMIT"))
                        .toList();
            }
            for (Order order : listOrder) {
                if (isNull(type) || order.getType().equals(type)) {
                    syncRequestClient.cancelOrder(symbol, order.getOrderId(), order.getClientOrderId());
                }
            }
        } catch (Exception e) {
            syncRequestClient.cancelAllOpenOrder(symbol);
        }
    }
}
