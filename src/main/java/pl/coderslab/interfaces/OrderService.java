package pl.coderslab.interfaces;

import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.model.CommonSignal;

import java.util.List;

public interface OrderService {
    Order getOrderBySymbol(User user, String symbol);

    List<Order> prepareOrderList(User user, List<Order> orders);

    void saveHistoryOrderToDB(User user, Order order, BinanceConfirmOrder binanceConfirmOrder, boolean ownClosed);

    void save(User user, CommonSignal signal, String entryPrice, String lot,
              String amount, String startProfit, int lev, Strategy strategy, boolean isOpen);
}
