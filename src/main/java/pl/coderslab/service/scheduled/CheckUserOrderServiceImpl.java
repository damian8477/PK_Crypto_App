package pl.coderslab.service.scheduled;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.trade.Order;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.Emoticon;
import pl.coderslab.interfaces.*;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.repository.OrderRepository;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CheckUserOrderServiceImpl implements CheckUserOrderService {
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
            if (!isNull(user.getOrders()) && userSettingService.userSettingExist(user.getUserSetting())) {
                SyncRequestClient syncRequestClient = syncService.sync(user.getUserSetting().get(0));
                List<PositionRisk> positions = syncRequestClient.getPositionRisk();
                List<PositionRisk> openedPositions = positions.stream()
                        .filter(s -> s.getPositionAmt().doubleValue() != 0.0)
                        .toList();
                user.getOrders().stream()
                        .filter(pl.coderslab.entity.orders.Order::isOpen)
                        .forEach(order -> {
                            PositionRisk position = openedPositions.stream()
                                    .filter(s -> s.getSymbol().equals(order.getSymbolName()))
                                    .filter(s -> s.getPositionSide().equals(order.getSide()))
                                    .findFirst().orElse(null);
                            if (isNull(position)) {
                                cancelAllOpenOrders(syncRequestClient, order.getSymbolName(), order.getSide());
                                orderRepository.deleteById(order.getId());
                                String message = String.format("Zlecenie zamknięte samodzielnie! %s $%s %s Lot: %s", Emoticon.OWN_CLOSED.getLabel(), order.getSymbolName(), order.getSide(), order.getLot());
                                BinanceConfirmOrder binanceConfirmOrder = binanceService.getBinanceConfirmOrder(syncRequestClient, Objects.requireNonNull(positions.stream().filter(s -> s.getPositionSide().equals(order.getSide()) && s.getSymbol().equals(order.getSymbolName())).findFirst().orElse(null)));
                                binanceConfirmOrder.setEntryPrice(order.getEntry());
                                binanceConfirmOrder.setLot(order.getLot());
                                orderService.saveHistoryOrderToDB(user, order, binanceConfirmOrder, true);
                                telegramBotService.sendMessage(user.getUserSetting().get(0).getTelegramChatId(), message);
                            }
                        });
            }
        });
    }
    @Override
    public void cancelAllOpenOrders(SyncRequestClient syncRequestClient, String symbol, String side) {
        try {
            List<Order> listOrder = syncRequestClient.getOpenOrders(symbol).stream()
                    .filter(s -> s.getPositionSide().equals(side))
                    .toList();
            for (Order order : listOrder) {
                syncRequestClient.cancelOrder(symbol, order.getOrderId(), order.getClientOrderId());
            }
        } catch (Exception e) {
            syncRequestClient.cancelAllOpenOrder(symbol);
        }
    }
}
