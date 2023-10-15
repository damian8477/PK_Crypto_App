package pl.coderslab.service.binance.orders;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.NewOrderRespType;
import pl.coderslab.binance.client.model.enums.OrderSide;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.Emoticon;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.repository.OrderRepository;
import pl.coderslab.service.binance.BinanceService;
import pl.coderslab.service.binance.BinanceBasicService;
import pl.coderslab.service.binance.OrderService;
import pl.coderslab.service.binance.SyncService;
import pl.coderslab.service.telegram.TelegramBotService;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CloseService {
    private final SyncService syncService;
    private final BinanceBasicService binanceUserService;
    private final BinanceService binanceService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final TelegramBotService telegramBotService;
    private static final Logger logger = LoggerFactory.getLogger(CloseService.class);


    public boolean closeOrderByUser(Order order, User user, String lot) {
        SyncRequestClient syncRequestClient = syncService.sync(user.getUserSetting().get(0));
        killOrder(syncRequestClient, order, user, lot);
        return true;
    }


    public boolean killOrder(SyncRequestClient syncRequestClient, Order order, User user, String lot) {
        PositionRisk position = syncRequestClient.getPositionRisk().stream()
                .filter(s -> s.getSymbol().equals(order.getSymbolName()))
                .filter(s -> s.getPositionSide().equals(order.getSide()))
                .filter(s -> s.getPositionAmt().doubleValue() != 0.0)
                .findFirst().orElse(null);
        if (!isNull(position)) {
            if (position.getPositionAmt().doubleValue() != 0.0) {
                killSymbol(syncRequestClient, position, lot);
                //todo logger
                if (order.isAppOrder()) {
                    orderRepository.deleteById(order.getId());
                    BinanceConfirmOrder binanceConfirmOrder = binanceService.getBinanceConfirmOrder(syncRequestClient, position);
                    orderService.saveHistoryOrderToDB(user, order, binanceConfirmOrder);
                    telegramBotService.sendMessage(user.getUserSetting().get(0).getTelegramChatId(), String.format("%s Zlecenie zamknięto w aplikacji! \n%s %s $%s %s $%s", Emoticon.CLOSE.getLabel(), order.getSymbolName(), order.getSide(), position.getMarkPrice(), Emoticon.getWinLoss(binanceConfirmOrder.getRealizedPln()), binanceConfirmOrder.getRealizedPln()));
                }
                return true;
            }
        }
        return false;
    }

    public void killSymbol(SyncRequestClient syncRequestClient, PositionRisk position, String lotSize) {
        String lot = String.valueOf(Math.abs(position.getPositionAmt().doubleValue()));
        if (lotSize != null) {
            if (Double.parseDouble(lotSize) <= Double.parseDouble(lot))
                lot = lotSize;
        }
        PositionSide positionSide = PositionSide.valueOf(position.getPositionSide());
        OrderSide orderSide = binanceUserService.getOrderSideForClose(positionSide);
        try {
            if (!binanceService.sendOrderToBinance(syncRequestClient, position.getSymbol(), orderSide, lot, position.getMarkPrice().toString(), positionSide, OrderType.MARKET)) {
                if (!binanceService.sendOrderToBinance(syncRequestClient, position.getSymbol(), orderSide, lot, position.getMarkPrice().toString(), positionSide,  OrderType.MARKET)) {
                    syncRequestClient.postOrder(position.getSymbol(), orderSide, positionSide, OrderType.MARKET, null,
                            lot, null, lot, null, null, null, NewOrderRespType.ACK);
                }
            }
            binanceService.cancelAllOpenOrders(syncRequestClient, position.getSymbol(), orderSide.toString());
        } catch (Exception e) {

        }
    }
}
