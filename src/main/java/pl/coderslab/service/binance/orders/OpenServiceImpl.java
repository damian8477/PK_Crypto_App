package pl.coderslab.service.binance.orders;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.OrderSide;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.market.ExchangeInfoEntry;
import pl.coderslab.binance.client.model.trade.MarginLot;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.binance.common.Common;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.Emoticon;
import pl.coderslab.interfaces.*;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.service.binance.SymbolExchangeService;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class OpenServiceImpl implements OpenService, Common {
    private final SourceService sourceService;
    private final BinanceBasicService binanceBasicService;
    private final BinanceService binanceService;
    private final SyncService syncService;
    private final OrderService orderService;
    private final TelegramBotService telegramBotService;
    private final MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(OpenServiceImpl.class);

    @Override
    @Transactional
    public void newSignal(CommonSignal signal) {
        Source source = sourceService.findByName(signal.getSourceName());
        double marketPrice = binanceBasicService.getMarketPriceDouble(null, signal.getSymbol());
        source.getStrategies().forEach(s -> {
            s.getUsers().forEach(user -> {
                if (user.isActive()) {
                    Strategy strategy = user.getStrategies().stream().filter(k -> k.getSource().getId().equals(source.getId())).findFirst().orElse(null);
                    ExchangeInfoEntry exchangeInfoEntry1 = SymbolExchangeService.exchangeInfoEntries.stream().filter(exchangeInfoEntry -> exchangeInfoEntry.getSymbol().equals(signal.getSymbol())).findFirst().orElse(null);
                    newOrder(signal, user.getOrders(), source, user, marketPrice, exchangeInfoEntry1, strategy);
                }
            });
        });
    }

    private void newOrder(CommonSignal signal, List<Order> orderList, Source source, User user, Double marketPrice, ExchangeInfoEntry exchangeInfoEntry, Strategy strategy) {
        SyncRequestClient syncRequestClient = syncService.sync(user.getUserSetting().get(0));
        PositionRisk positionRisk = syncRequestClient.getPositionRisk().stream()
                .filter(s -> s.getSymbol().equals(signal.getSymbol()))
                .filter(s -> s.getPositionSide().equals(signal.getPositionSide().toString()))
                .filter(s -> s.getPositionAmt().doubleValue() != 0.0)
                .findFirst().orElse(null);
        if (isNull(positionRisk) && !orderList.stream().anyMatch(s -> s.getSymbolName().equals(signal.getSymbol()))) {
            openSignal(signal, syncRequestClient, exchangeInfoEntry, user, marketPrice, source, strategy);
        }
    }

    private void openSignal(CommonSignal signal, SyncRequestClient sync, ExchangeInfoEntry exchangeInfoEntry, User user, double marketPrice, Source source, Strategy strategy) {
        try {
            boolean okOrder = newOrderForUserSlidingHedge(signal, sync, marketPrice, source, strategy, exchangeInfoEntry, user);
            if (okOrder)
                logger.info(getStringFormat("%s %s %s %s order", user.getUsername(), getTimeStamp(), signal.getSymbol(),source.getName()));
        } catch (Exception e) {
            logger.error(getStringFormat("%s %s %s %s order %s", user.getUsername(), getTimeStamp(), signal.getSymbol(), source.getName(), e.getMessage()));
        }
    }

    public boolean newOrderForUserSlidingHedge(CommonSignal signal, SyncRequestClient sync, double marketPrice, Source source, Strategy strategy, ExchangeInfoEntry exchangeInfoEntry, User user) {
        OrderSide orderSide = binanceBasicService.getOrderSideForOpen(signal.getPositionSide());
        PositionSide positionSide = signal.getPositionSide();
        setMarginType(sync, signal.getMarginType(), signal.getSymbol());
        int leverage = getLeverageSource(List.of(signal.getLever(), strategy.getMaxLeverage(), source.getMaxLeverage()));
        int lever = setLeverageF(sync, leverage, signal.getSymbol());
        double amountValue = getAmountValue(signal.getSymbol(), sync, strategy);
        double minQty = getMinQty(signal.getSymbol(), sync, exchangeInfoEntry);
        int lengthPrice = getLengthPrice(sync, signal.getSymbol());

        MarginLot marginLot = calculateLotSizeQuantityMargin(signal.getSymbol(), amountValue, lever, sync, marketPrice, exchangeInfoEntry);
        boolean isOpen = false;
        if ((signal.getPositionSide().equals(PositionSide.LONG) && marketPrice > signal.getEntryPrice().get(0).doubleValue()) || (signal.getPositionSide().equals(PositionSide.SHORT) && marketPrice < signal.getEntryPrice().get(0).doubleValue())) {
            signal.setOrderType(OrderType.MARKET);
        }
        if (signal.getOrderType().equals(OrderType.MARKET)) isOpen = true;
        if (binanceService.sendOrderToBinance(sync, signal.getSymbol(), orderSide, marginLot.getLot(), String.valueOf(marketPrice), positionSide, signal.getOrderType(), signal.getEntryPrice().get(0).toString())) {
            binanceService.sendSlAndTpToAccountMultipleTp(sync, signal.getSymbol(), positionSide, signal.getStopLoss().get(0).toString(),
                    signal.getTakeProfit(), minQty, lever, marketPrice, signal.getOrderType(), lengthPrice, marginLot.getLot());
            orderService.save(user, signal, String.valueOf(marketPrice), marginLot.getLot(), "", "", lever, strategy, isOpen, source, true);
            telegramBotService.sendMessage(user.getUserSetting().get(0).getTelegramChatId(), String.format(messageService.getOrderOpenSignal(null), Emoticon.OPEN.getLabel(), signal.getSymbol(), Emoticon.valueOf(signal.getPositionSide().toString()), marketPrice, signal.getLot()));
            logger.info(String.format("Username: %s\n%s Zlecenie otwarte! \n%s %s $%s LOT: $%s", user.getUsername(), Emoticon.OPEN.getLabel(), signal.getSymbol(), Emoticon.valueOf(signal.getPositionSide().toString()), marketPrice, signal.getLot()));
            return true;
        }
        return false;
    }
}
