package pl.coderslab.interfaces;

import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.entity.user.User;

import java.util.List;

public interface CheckUserOrderService {
    void checkInActiveOrder(List<User> users);

    void cancelAllOpenOrders(SyncRequestClient syncRequestClient, String symbol, String side);
}
