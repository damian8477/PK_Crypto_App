package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.entity.orders.HistoryOrder;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.orders.Signal;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.repository.HistoryOrderRepository;
import pl.coderslab.repository.OrderRepository;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final SyncService syncService;
    private final HistoryOrderRepository historyOrderRepository;

    public Order getOrderBySymbol(User user, String symbol) {
        SyncRequestClient syncRequestClient = syncService.sync(user.getUserSetting().get(0));
        PositionRisk positionRisk = syncRequestClient.getPositionRisk().stream()
                .filter((s -> s.getPositionAmt().doubleValue() != 0.0))
                .filter(s -> s.getSymbol().equals(symbol)).findFirst().orElse(null);
        if (positionRisk == null) {
            return null;
        }
        return Order.builder()
                .symbolName(positionRisk.getSymbol())
                .appOrder(false)
                .id(0L)
                .isStrategy(false)
                .sl("")//todo tutaj moze pobrac zlecenia do danego coina
                .tp("")//todo
                .user(user)
                .side(positionRisk.getPositionSide())
                .lot(positionRisk.getPositionAmt().toString())
                .entry(positionRisk.getEntryPrice().toString())
                .amount(String.valueOf(Math.round(100.0 * positionRisk.getEntryPrice().doubleValue() * positionRisk.getPositionAmt().doubleValue() / positionRisk.getLeverage().doubleValue()) / 100.0))
                .profitProcent(positionRisk.getUnrealizedProfit().doubleValue())
                .build();
    }

    public List<Order> prepareOrderList(User user, List<Order> orders) {
        if (user.getUserSetting().get(0) == null && orders == null) {
            return new ArrayList<>();
        } else if (user.getUserSetting().get(0) == null) {
            return orders;
        } else {
            return addOwnOrder(user, orders);
        }
    }

    private List<Order> addOwnOrder(User user, List<Order> orderList) {
        SyncRequestClient syncRequestClient = syncService.sync(user.getUserSetting().get(0));
        List<PositionRisk> positionRiskList = syncRequestClient.getPositionRisk().stream()
                .filter((s -> s.getPositionAmt().doubleValue() != 0.0))
                .filter(s -> !orderList.stream().anyMatch(order -> order.getSymbolName().equals(s.getSymbol())))
                .toList();
        positionRiskList.forEach(s -> {
            orderList.add(Order.builder()
                    .symbolName(s.getSymbol())
                    .appOrder(false)
                    .id(0L)
                    .isStrategy(false)
                    .sl("")//todo tutaj moze pobrac zlecenia do danego coina
                    .tp("")//todo
                    .user(user)
                    .side(s.getPositionSide())
                    .lot(s.getPositionAmt().toString())
                    .entry(s.getEntryPrice().toString())
                    .amount(String.valueOf(Math.round(100.0 * s.getEntryPrice().doubleValue() * s.getPositionAmt().doubleValue() / s.getLeverage().doubleValue()) / 100.0))
                    .profitProcent(s.getUnrealizedProfit().doubleValue())

                    .build());
        });
        return orderList;
    }


    public void saveHistoryOrderToDB(User user, Order order, BinanceConfirmOrder binanceConfirmOrder) {
        historyOrderRepository.save(
                HistoryOrder.builder()
                        .symbol(order.getSymbolName())
                        .entry(binanceConfirmOrder.getEntryPrice())
                        .close(binanceConfirmOrder.getClosePrice())
                        .lot(binanceConfirmOrder.getLot())
                        .side(order.getSide())
                        .amount(getAmount(order.getLeverage(), binanceConfirmOrder.getEntryPrice(), binanceConfirmOrder.getLot()))
                        .leverage(order.getLeverage())
                        .commission(binanceConfirmOrder.getCommission())
                        .realizedPln(binanceConfirmOrder.getRealizedPln())
                        .profitPercent(getProfitPercent(binanceConfirmOrder.getEntryPrice(), binanceConfirmOrder.getClosePrice(), order.getLeverage()))
                        .strategy(order.getStrategy())
                        .user(user)
                        .signal(order.getSignal())
                        .build());

    }

    public void saveOrderToDB(User user, CommonSignal signal, String entryPrice, String lot,
                              String amount, String startProfit, int lev, Strategy strategy) {
        orderRepository.save(Order.builder()
                .user(user)
                .symbolName(signal.getSymbol())
                .tp(signal.getTakeProfit().get(0))
                .sl(signal.getStopLoss().get(0))
                .entry(entryPrice)
                .lot(lot)
                .side(signal.getPositionSide().toString())
                .strategy(strategy)
                .profitProcent(0.0)
                .amount(amount)
                .startProfit(startProfit)
                .leverage(lev)
                .appOrder(true)
                .build());

    }

    private BigDecimal getProfitPercent(String entry, String close, int lev) {
        double percent = ((Math.abs(Double.parseDouble(entry) - Double.parseDouble(close)) / Double.parseDouble(entry)) * 100.0) * lev;
        return BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getAmount(int lev, String entry, String lot) {
        double amount = (Double.parseDouble(entry) * Double.parseDouble(lot)) / lev;
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }
}
