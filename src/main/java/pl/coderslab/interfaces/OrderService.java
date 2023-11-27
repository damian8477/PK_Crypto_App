package pl.coderslab.interfaces;

import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.orders.Signal;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.model.CommonSignal;

import java.util.List;

public interface OrderService {
    Order getOrderBySymbol(User user, String symbol);

    List<Order> prepareOrderList(User user, List<Order> orders);

    void saveHistoryOrderToDB(User user, Order order, BinanceConfirmOrder binanceConfirmOrder, boolean ownClosed, boolean win);

    void save(User user, CommonSignal commonSignal, String entryPrice, String lot,
              String amount, String startProfit, int lev, Strategy strategy, boolean isOpen, Source source, boolean isStrategy);
    void save(User user, Signal signal, String lot, String amount, String startProfit, int lev, Strategy strategy, boolean isOpen, Source source, boolean isStrategy);

    void update(Order order);

    void deleteById(Long id);

    List<Order> findByUserId(Long id);
}
