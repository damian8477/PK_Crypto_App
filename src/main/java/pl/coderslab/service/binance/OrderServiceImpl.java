package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.entity.orders.HistoryOrder;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.orders.Signal;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;
import pl.coderslab.interfaces.OrderService;
import pl.coderslab.interfaces.SyncService;
import pl.coderslab.interfaces.UserSettingService;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.repository.HistoryOrderRepository;
import pl.coderslab.repository.OrderRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final SyncService syncService;
    private final HistoryOrderRepository historyOrderRepository;
    private final UserSettingService userSettingService;

    @Override
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
                .positionSide(PositionSide.valueOf(positionRisk.getPositionSide()))
                .lot(positionRisk.getPositionAmt().toString())
                .entry(positionRisk.getEntryPrice().toString())
                .amount(String.valueOf(Math.round(100.0 * positionRisk.getEntryPrice().doubleValue() * positionRisk.getPositionAmt().doubleValue() / positionRisk.getLeverage().doubleValue()) / 100.0))
                .profitProcent(positionRisk.getUnrealizedProfit().doubleValue())
                .build();
    }

    @Override
    public List<Order> prepareOrderList(User user, List<Order> orders) {
        if (userSettingService.userSettingExist(user.getUserSetting()) && isNull(orders)) {
            return new ArrayList<>();
        } else if (!userSettingService.userSettingExist(user.getUserSetting())) {
            return orders;
        } else {
            return addOwnOrder(user, orders);
        }
    }

    private List<Order> addOwnOrder(User user, List<Order> orderList) {
        SyncRequestClient syncRequestClient = syncService.sync(user.getUserSetting().get(0));
        List<PositionRisk> positionRiskList = syncRequestClient.getPositionRisk().stream()
                .filter((s -> s.getPositionAmt().doubleValue() != 0.0))
                .toList();
        fillProfit(orderList, positionRiskList);
        positionRiskList.stream()
                .filter(s -> !orderList.stream().anyMatch(order -> order.getSymbolName().equals(s.getSymbol())))
                .forEach(s -> {
                    orderList.add(Order.builder()
                            .symbolName(s.getSymbol())
                            .appOrder(false)
                            .id(0L)
                            .isStrategy(false)
                            .sl("")//todo tutaj moze pobrac zlecenia do danego coina
                            .tp("")//todo
                            .user(user)
                            .positionSide(PositionSide.valueOf(s.getPositionSide()))
                            .lot(s.getPositionAmt().toString())
                            .entry(s.getEntryPrice().toString())
                            .leverage(s.getLeverage().intValue())
                            .amount(String.valueOf(Math.round(100.0 * s.getEntryPrice().doubleValue() * s.getPositionAmt().doubleValue() / s.getLeverage().doubleValue()) / 100.0))
                            .profitProcent(s.getUnrealizedProfit().setScale(2, RoundingMode.HALF_UP).doubleValue())
                            .build());
                });
        return orderList;
    }

    private void fillProfit(List<Order> orderList, List<PositionRisk> positionRiskList) {
        orderList.forEach(order -> {
            PositionRisk position = positionRiskList.stream()
                    .filter(s -> s.getSymbol().equals(order.getSymbolName())).findFirst().orElse(null);
            if (!isNull(position)) {
                order.setProfitProcent(position.getUnrealizedProfit().setScale(2, RoundingMode.HALF_UP).doubleValue());
                order.setLeverage(position.getLeverage().intValue());
                order.setAmount(String.valueOf(Math.round(100.0 * position.getEntryPrice().doubleValue() * position.getPositionAmt().doubleValue() / position.getLeverage().doubleValue()) / 100.0));

            }
        });
    }

    @Override
    public void saveHistoryOrderToDB(User user, Order order, BinanceConfirmOrder binanceConfirmOrder, boolean ownClosed, boolean win) {
        historyOrderRepository.save(
                HistoryOrder.builder()
                        .symbol(order.getSymbolName())
                        .entry(binanceConfirmOrder.getEntryPrice())
                        .close(binanceConfirmOrder.getClosePrice())
                        .lot(binanceConfirmOrder.getLot())
                        .side(order.getPositionSide().toString())
                        .leverage(order.getLeverage())
                        .commission(binanceConfirmOrder.getCommission())
                        .realizedPln(binanceConfirmOrder.getRealizedPln())
                        .profitPercent(getProfitPercent(binanceConfirmOrder.getEntryPrice(), binanceConfirmOrder.getClosePrice(), order.getLeverage()))
                        .strategy(null)
                        .user(user)
                        .ownClosed(ownClosed)
                        .signal(order.getSignal())
                        .win(win)
                        .build());
    }

    @Override
    public void save(User user, CommonSignal commonSignal, String entryPrice, String lot,
                     String amount, String startProfit, int lev, Strategy strategy, boolean isOpen, Source source, boolean isStrategy) {
        orderRepository.save(Order.builder()
                .user(user)
                .symbolName(commonSignal.getSymbol())
                .tp(commonSignal.getTakeProfit().get(0).toString())
                .sl(commonSignal.getStopLoss().get(0).toString())
                .entry(entryPrice)
                .lot(lot)
                .positionSide(PositionSide.valueOf(commonSignal.getPositionSide().toString()))
                .strategy(strategy)
                .source(source)
                .profitProcent(0.0)
                .amount(amount)
                .startProfit(startProfit)
                .leverage(lev)
                .open(isOpen)
                .appOrder(true)
                .signal(commonSignal.getSignal())
                .isStrategy(isStrategy)
                .build());

    }

    @Override
    public void save(User user, Signal signal, String lot,
                     String amount, String startProfit, int lev, Strategy strategy, boolean isOpen, Source source, boolean isStrategy) {
        orderRepository.save(Order.builder()
                .user(user)
                .symbolName(signal.getSymbol())
                .tp(signal.getTakeProfit1().toString())
                .sl(signal.getStopLoss().toString())
                .entry(signal.getEntryPrice().toString())
                .lot(lot)
                .positionSide(PositionSide.valueOf(signal.getPositionSide().toString()))
                .strategy(strategy)
                .source(source)
                .profitProcent(0.0)
                .amount(amount)
                .startProfit(startProfit)
                .leverage(lev)
                .open(isOpen)
                .appOrder(true)
                .signal(signal)
                .isStrategy(isStrategy)
                .build());

    }

    @Override
    public void update(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<Order> findByUserId(Long id) {
        return orderRepository.findByUserId(id);
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
