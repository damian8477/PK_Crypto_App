package pl.coderslab.interfaces;

import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.strategy.CCIOrder;

import java.util.List;

public interface StrategyService {
    void searchNewOrder(List<Order> orders);

    void checkOrderStatusBot(List<Order> activeOrdersListArg);

    void downloadSymbols();

    void updateStrategyDb();
}
