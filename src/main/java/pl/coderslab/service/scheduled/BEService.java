package pl.coderslab.service.scheduled;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;
import pl.coderslab.interfaces.BinanceService;
import pl.coderslab.interfaces.OrderService;
import pl.coderslab.interfaces.SyncService;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class BEService {
    private final OrderService orderService;
    private final BinanceService binanceService;

    public void checkBE(SyncRequestClient syncRequestClient, PositionRisk positionRisk, Order order) {
        Source source = order.getSource();
        if (source.isWalkingStopLoss() && !order.isBe()) {
            double bePercent = getBePercent(source);
            double entryPrice = positionRisk.getEntryPrice().doubleValue();
            double marketPrice = positionRisk.getMarkPrice().doubleValue();
            if (positionRisk.getPositionSide().equals("LONG") && Math.abs((marketPrice - entryPrice) / entryPrice * 100) > bePercent) {
                setSlOnBE(syncRequestClient, order, positionRisk, entryPrice);
            }
            if (positionRisk.getPositionSide().equals("SHORT") && Math.abs((entryPrice - marketPrice) / entryPrice * 100) > bePercent) {
                setSlOnBE(syncRequestClient, order, positionRisk, entryPrice);
            }
        }
    }

    private void setSlOnBE(SyncRequestClient syncRequestClient, Order order, PositionRisk positionRisk, double slPrice) {
        order.setBe(true);
        binanceService.changeStopLoss(syncRequestClient, order, positionRisk.getPositionAmt().toString(), slPrice);
        orderService.save(order);
    }

    private double getBePercent(Source source) {
        if (!isNull(source.getBePercent())) {
            if (source.getBePercent().doubleValue() <= 0.0) {
                return 0.5;
            }
        } else {
            return 0.5;
        }
        return source.getBePercent().doubleValue();
    }
}
