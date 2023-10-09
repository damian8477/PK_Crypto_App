package pl.coderslab.service.binance.orders;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.http11.Constants;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.NewOrderRespType;
import pl.coderslab.binance.client.model.enums.OrderSide;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.user.User;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.repository.OrderRepository;
import pl.coderslab.service.binance.BinanceService;
import pl.coderslab.service.binance.BinanceUserService;
import pl.coderslab.service.binance.OrderService;
import pl.coderslab.service.binance.SyncService;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CloseService {
    private final SyncService syncService;
    private final BinanceUserService binanceUserService;
    private final BinanceService binanceService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public boolean closeOrderByUser(Order order, User user) {
        SyncRequestClient syncRequestClient = syncService.sync(user.getUserSetting().get(0));
        killOrder(syncRequestClient, order, user);
        return true;//todo
    }

    public boolean killOrder(SyncRequestClient syncRequestClient, Order order, User user) {
        PositionRisk positionRisk = syncRequestClient.getPositionRisk().stream()
                .filter(s -> s.getSymbol().equals(order.getSymbolName()))
                .filter(s -> s.getPositionSide().equals(order.getSide()))
                .filter(s -> s.getPositionAmt().doubleValue() != 0.0)
                .findFirst().orElse(null);
        if (!isNull(positionRisk)) {
            if (positionRisk.getPositionAmt().doubleValue() != 0.0) {
                killSymbol(syncRequestClient, positionRisk);
                //todo telegram , info ze zamknieto zlecenie
                //todo logger
                //todo zapis historii
                orderRepository.deleteById(order.getId());
                BinanceConfirmOrder binanceConfirmOrder = binanceService.getBinanceConfirmOrder(syncRequestClient, positionRisk.getSymbol());
                String marketPrice = binanceUserService.getMarketPriceString(syncRequestClient, positionRisk.getSymbol()); //todo z positionRisk mozna wziac
                orderService.saveHistoryOrderToDB(user, order, binanceConfirmOrder);
                //usuniecie orderu z bazy
                return true;
            }


        }
        return false;
    }

    public void killSymbol(SyncRequestClient syncRequestClient, PositionRisk position) {
        String lot = String.valueOf(Math.abs(position.getPositionAmt().doubleValue()));
        PositionSide positionSide = PositionSide.valueOf(position.getPositionSide());
        OrderSide orderSide = binanceUserService.getOrderSideForClose(positionSide, 0.0);
        try {
            if (!binanceService.sendOrderToBinance(syncRequestClient, position.getSymbol(), orderSide, lot, position.getMarkPrice().toString(), positionSide)) {
                if (!binanceService.sendOrderToBinance(syncRequestClient, position.getSymbol(), orderSide, lot, position.getMarkPrice().toString(), positionSide)) {
                    syncRequestClient.postOrder(position.getSymbol(), orderSide, positionSide, OrderType.MARKET, null,
                            lot, null, lot, null, null, null, NewOrderRespType.ACK);
                }
            }
            binanceService.cancelAllOpenOrders(syncRequestClient, position.getSymbol(), orderSide.toString());
        } catch (Exception e) {

        }


    }
}
