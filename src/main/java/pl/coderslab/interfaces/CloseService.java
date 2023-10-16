package pl.coderslab.interfaces;

import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.user.User;

public interface CloseService {
    boolean closeOrderByUser(Order order, User user, String lot);

    boolean killOrder(SyncRequestClient syncRequestClient, Order order, User user, String lot);

    void killSymbol(SyncRequestClient syncRequestClient, PositionRisk position, String lotSize);
}
