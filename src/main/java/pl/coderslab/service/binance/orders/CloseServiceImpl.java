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
import pl.coderslab.interfaces.*;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CloseServiceImpl implements CloseService {
    private final SyncService syncService;
    private final BinanceBasicService binanceSupport;
    private final BinanceService binanceService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final TelegramBotService telegramBotService;
    private final MessageService messageService;
    private static final Logger logger = LoggerFactory.getLogger(CloseServiceImpl.class);

    @Override
    public boolean closeOrderByUser(Order order, User user, String lot) {
        SyncRequestClient syncRequestClient = syncService.sync(user.getUserSetting().get(0));
        killOrder(syncRequestClient, order, user, lot);
        return true;
    }

    @Override
    public boolean killOrder(SyncRequestClient syncRequestClient, Order order, User user, String lot) {
        List<PositionRisk> positions = syncRequestClient.getPositionRisk().stream()
                .filter(s -> s.getSymbol().equals(order.getSymbolName()))
                .filter(s -> s.getPositionSide().equals(order.getPositionSide().toString()))
                .toList();
        PositionRisk position = positions.stream()
                .filter(s -> s.getPositionAmt().doubleValue() != 0.0)
                .findFirst().orElse(null);
        double marketPrice = positions.stream()
                .findFirst()
                .map(PositionRisk::getMarkPrice)
                .map(BigDecimal::doubleValue)
                .orElse(0.0);
        if (!isNull(position)) {
            if (position.getPositionAmt().doubleValue() != 0.0) {
                killSymbol(syncRequestClient, position, lot);
                if (order.isAppOrder()) {
                    orderRepository.deleteById(order.getId());
                    BinanceConfirmOrder binanceConfirmOrder = binanceService.getBinanceConfirmOrder(syncRequestClient, order, marketPrice);
                    boolean win = false;
                    if(binanceConfirmOrder.getRealizedPln().compareTo(new BigDecimal("0.0")) > 0){
                        win = true;
                    }
                    orderService.saveHistoryOrderToDB(user, order, binanceConfirmOrder, false, win);
                    telegramBotService.sendMessage(user.getUserSetting().get(0).getTelegramChatId(), String.format(messageService.getOrderCloseOwn(null), Emoticon.CLOSE.getLabel(), order.getSymbolName(), order.getPositionSide(), position.getMarkPrice(), Emoticon.getWinLoss(binanceConfirmOrder.getRealizedPln()), binanceConfirmOrder.getRealizedPln()));
                    logger.info(String.format(messageService.getOrderCloseOwn(null), Emoticon.CLOSE.getLabel(), order.getSymbolName(), order.getPositionSide(), position.getMarkPrice(), Emoticon.getWinLoss(binanceConfirmOrder.getRealizedPln()), binanceConfirmOrder.getRealizedPln()));
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public void killSymbol(SyncRequestClient syncRequestClient, PositionRisk position, String lotSize) {
        String lot = String.valueOf(Math.abs(position.getPositionAmt().doubleValue()));
        if (lotSize != null) {
            if (Double.parseDouble(lotSize) <= Double.parseDouble(lot))
                lot = lotSize;
        }
        PositionSide positionSide = PositionSide.valueOf(position.getPositionSide());
        OrderSide orderSide = binanceSupport.getOrderSideForClose(positionSide);
        try {
            if (!binanceService.sendOrderToBinance(syncRequestClient, position.getSymbol(), orderSide, lot, position.getMarkPrice().toString(), positionSide, OrderType.MARKET, null)) {
                if (!binanceService.sendOrderToBinance(syncRequestClient, position.getSymbol(), orderSide, lot, position.getMarkPrice().toString(), positionSide, OrderType.MARKET, null)) {
                    syncRequestClient.postOrder(position.getSymbol(), orderSide, positionSide, OrderType.MARKET, null,
                            lot, null, lot, null, null, null, NewOrderRespType.ACK);
                }
            }
            binanceSupport.cancelOpenOrder(syncRequestClient, position.getSymbol(), orderSide);
        } catch (Exception e) {
            logger.error(String.format("Error during kill order %s", e));
        }
    }
}
