package pl.coderslab.service.scheduled;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.OrderSide;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.trade.Order;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.Emoticon;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.repository.OrderRepository;
import pl.coderslab.service.binance.BinanceBasicService;
import pl.coderslab.service.binance.BinanceService;
import pl.coderslab.service.binance.OrderService;
import pl.coderslab.service.binance.SyncService;
import pl.coderslab.service.entity.UserService;
import pl.coderslab.service.entity.UserSettingService;
import pl.coderslab.service.telegram.RequestTelegramService;
import pl.coderslab.service.telegram.TelegramBotService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CheckUserOrderService {
    private final SyncService syncService;
    private final BinanceBasicService binanceBasicService;
    private final BinanceService binanceService;
    private final TelegramBotService telegramBotService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserSettingService userSettingService;
    private static final Logger logger = LoggerFactory.getLogger(CheckUserOrderService.class);

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
                                orderService.saveHistoryOrderToDB(user, order, binanceConfirmOrder);
                                telegramBotService.sendMessage(user.getUserSetting().get(0).getTelegramChatId(), message);
                            }
                        });
            }
        });
    }

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
//    public void deleteInActiveOrder(List<ActiveOrders> activeOrdersMain, List<Users> users, List<PositionsRiskUser> positionRiskListUsers, List<MarketPriceSymbol> marketPriceList, List<OpenOrders> ordersOpenOnBinance) {
//        try {
//
//            List<ActiveOrders> activeOrdersList = activeOrdersMain.stream()
//                    .filter(s -> (s.getSourceId() > 100 && s.getSourceId() < 125 && s.getSourceId() != 1000) || (s.getSourceId() >= 200 && s.getSourceId() != 1000))
//                    .filter(s -> s.getUserId() != 1000).toList();//
//
//            List<ActiveOrders> activeOrders125 = activeOrdersMain.stream()
//                    .filter(s -> (s.getSourceId() > 124 && s.getSourceId() <= 199) || s.getSourceId() == 1000)
//                    .filter(s -> s.getUserId() != 1000).toList();//
//            for (ActiveOrders activeOrders : activeOrders125) {
//                try {
//                    Users user = users.stream().filter(s -> s.getId() == activeOrders.getUserId()).toList().get(0);
//                    List<PositionRisk> positionRiskList = new ArrayList<>();
//
//                    positionRiskList = positionRiskListUsers.stream()
//                            .filter(s -> s.getUser().getId() == user.getId())
//                            .map(PositionsRiskUser::getPositionRiskList)
//                            .findFirst().get();
//
//                    List<String> positionSymbol = positionRiskList.stream()
//                            .filter((s -> s.getPositionAmt().doubleValue() != 0.0))
//                            .filter(s -> (s.getPositionAmt().doubleValue() > 0 && activeOrders.getSide().equals("BUY")) || (s.getPositionAmt().doubleValue() < 0 && activeOrders.getSide().equals("SELL")))
//                            .map(PositionRisk::getSymbol).toList();
//
//                    if (!positionSymbol.contains(activeOrders.getCurrencyPair())) {
//                        if (idToKill.contains(activeOrders.getId())) {
//                            requestBinanceService.deleteOpenOrdersOnAccount(user, activeOrders.getCurrencyPair(), activeOrders.getSide());
//                            String marketPrice = marketPriceList.stream()
//                                    .filter(s -> s.getSymbol().equals(activeOrders.getCurrencyPair()))
//                                    .map(MarketPriceSymbol::getMarketPrice).findFirst().get();//getMarketPriceCrypto(activeOrders.getCurrencyPair());
//                            requestTelegram.sentInfoToTelegram(Constants.CLOSE,
//                                    calculateProfit(Double.parseDouble(activeOrders.getLot()), Double.parseDouble(activeOrders.getCurrentEntryPrice()), Double.parseDouble(marketPrice), activeOrders.getSide(), activeOrders.getLeverage()),
//                                    "-", "SL, Own close " + activeOrders.getCurrencyPair(), user.getId(), 0, 20, activeOrders.getSide(), activeOrders.getSourceId());
//                            requestDbService.saveHistoryOrders(getHistoryOrders(activeOrders, marketPrice, "deleteInActiveOrder - strateyAc 1", 0.0, 0.0));
//                            requestDbService.deleteActiveOrdersById(activeOrders.getId());
//                            idToKill.remove(activeOrders.getId());
//                        }
//                        idToKill.add(activeOrders.getId());
//                    }
//                } catch (Exception e) {
//                    System.out.println("InActiveOrder " + e);
//                }
//
//            }
//
//            for (ActiveOrders activeOrders : activeOrdersList) {
//                if (activeOrders.isStrategy() && activeOrders.getStartProfit().contains("SLIDING")) {
//                } else {
//                    Users user = users.stream().filter(s -> s.getId() == activeOrders.getUserId()).findFirst().get();
//
//                    List<PositionRisk> positionRiskList = positionRiskListUsers.stream()
//                            .filter(s -> s.getUser().getId() == user.getId())
//                            .map(PositionsRiskUser::getPositionRiskList)
//                            .findFirst().get();
//
//                    List<String> positionSymbol = positionRiskList.stream()
//                            .filter((s -> s.getPositionAmt().doubleValue() != 0.0))
//                            .filter(s -> (s.getPositionAmt().doubleValue() > 0 && activeOrders.getSide().equals("BUY")) || (s.getPositionAmt().doubleValue() < 0 && activeOrders.getSide().equals("SELL")))
//                            .map(PositionRisk::getSymbol).toList();
//
//                    if (!positionSymbol.contains(activeOrders.getCurrencyPair())) {
//                        requestBinanceService.deleteOpenOrdersOnAccount(user, activeOrders.getCurrencyPair(), activeOrders.getSide());
//                        String marketPrice = marketPriceList.stream()
//                                .filter(s -> s.getSymbol().equals(activeOrders.getCurrencyPair()))
//                                .map(MarketPriceSymbol::getMarketPrice)
//                                .findFirst().get();
//                        requestDbService.saveHistoryOrders(getHistoryOrders(activeOrders, marketPrice, "deleteInActiveOrder - strateyAc 3", 0.0, 0.0));
//                        requestDbService.deleteActiveOrdersById(activeOrders.getId());
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("ttt " + e);
//        }
//    }
}
