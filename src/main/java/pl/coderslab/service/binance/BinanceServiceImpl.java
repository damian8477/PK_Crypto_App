package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.*;
import pl.coderslab.binance.client.model.market.Candlestick;
import pl.coderslab.binance.client.model.market.ExchangeInfoEntry;
import pl.coderslab.binance.client.model.trade.Income;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.binance.common.Common;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.enums.Emoticon;
import pl.coderslab.interfaces.*;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.model.CryptoName;
import pl.coderslab.repository.SymbolRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class BinanceServiceImpl implements BinanceService, Common {

    private final SyncService syncService;
    private final SymbolRepository symbolRepository;
    private final BinanceBasicService binanceSupport;
    private final OrderService orderService;
    private final TelegramBotService telegramBotService;
    private final MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(BinanceServiceImpl.class);

    @Override
    public List<CryptoName> getSymbols() {
        List<CryptoName> cryptoNameList = new ArrayList<>();
        List<Symbol> symbolList = symbolRepository.findAll();
        SyncRequestClient syncRequestClient = syncService.sync(null);
        symbolList.forEach(s -> {
            List<Candlestick> candlestick = syncRequestClient.getCandlestick(s.getName(), CandlestickInterval.DAILY, null, null, 1);
            if (!candlestick.isEmpty()) {
                Candlestick cd = candlestick.get(0);
                cryptoNameList.add(new CryptoName(
                        s.getId(),
                        s.getName(),
                        cd.getClose(),
                        false,
                        cd.getLow(),
                        cd.getHigh(),
                        ((cd.getClose().subtract(cd.getOpen())).multiply(BigDecimal.valueOf(100.0)).divide(cd.getClose(), 2, RoundingMode.HALF_UP))
                ));
            }
        });
        return cryptoNameList;
    }

    @Override
    public List<String> getAllSymbol() {
        SyncRequestClient syncRequestClient = syncService.sync(null);
        return syncRequestClient.getExchangeInformation().getSymbols().stream()
                .map(ExchangeInfoEntry::getSymbol).toList();
    }

    @Override
    public CryptoName getSymbols(int symbolId) {
        Symbol symbol = symbolRepository.findById(symbolId);
        if (symbol != null) {
            List<Candlestick> candlestick = syncService.sync(null).getCandlestick(symbol.getName(), CandlestickInterval.DAILY, null, null, 1);
            if (!candlestick.isEmpty()) {
                Candlestick cd = candlestick.get(0);
                return new CryptoName(
                        symbol.getId(),
                        symbol.getName(),
                        cd.getClose(),
                        false,
                        cd.getLow(),
                        cd.getHigh(),
                        ((cd.getClose().subtract(cd.getOpen())).multiply(BigDecimal.valueOf(100.0)).divide(cd.getClose(), 2, RoundingMode.HALF_UP))
                );
            }
        }
        return new CryptoName();
    }

    @Override
    public List<String> getSymbolNames() {
        return syncService.sync(null).getPositionRisk().stream()
                .map(PositionRisk::getSymbol)
                .toList();
    }

    @Override
    public boolean createOrder(CommonSignal signal, User user, SyncRequestClient syncRequestClient) {
        OrderSide orderSide = binanceSupport.getOrderSideForOpen(signal.getPositionSide());
        PositionSide positionSide = signal.getPositionSide();
        binanceSupport.setMarginType(syncRequestClient, signal.getMarginType(), signal.getSymbol());
        int lever = binanceSupport.setLeverage(syncRequestClient, signal.getLever(), signal.getSymbol());
        String marketPrice = binanceSupport.getMarketPriceString(syncRequestClient, signal.getSymbol());
        String lot = signal.getLot();
        boolean isOpen = signal.getOrderType().equals(OrderType.MARKET);
        if (sendOrderToBinance(syncRequestClient, signal.getSymbol(), orderSide, lot, marketPrice, positionSide, signal.getOrderType(), null)) {
            if (isOpen) {
                sendSlAndTpToAccount(syncRequestClient, signal.getSymbol(), orderSide, positionSide, signal.getStopLoss().get(0).toString(), signal.getTakeProfit().get(0).toString());
            }
            orderService.save(user, signal, marketPrice, lot, "", "", lever, null, isOpen, null, false);
            telegramBotService.sendMessage(user.getUserSetting().get(0).getTelegramChatId(), String.format(messageService.getOrderOpenSignal(null), Emoticon.OPEN.getLabel(), signal.getSymbol(), Emoticon.valueOf(signal.getPositionSide().toString()), marketPrice, signal.getLot()));
            logger.info(getStringFormat("Username: %s%n%s Zlecenie otwarte! %n%s %s $%s LOT: $%s", user.getUsername(), Emoticon.OPEN.getLabel(), signal.getSymbol(), Emoticon.valueOf(signal.getPositionSide().toString()), marketPrice, signal.getLot()));
            return true;
        }
        return false;
    }

    @Override
    public SyncRequestClient sync(UserSetting userSetting) {
        return syncService.sync(userSetting);
    }

    @Override
    public boolean sendSlAndTpToAccount(SyncRequestClient syncRequestClient, String cryptoName, OrderSide orderSide, PositionSide positionSide, String stopLoss, String takeProfit) {
        try {
            OrderSide orderCloseSide = getOrderSideClose(positionSide);
            binanceSupport.cancelOpenOrder(syncRequestClient, cryptoName, orderCloseSide, null);
            PositionRisk positionRisk = syncRequestClient.getPositionRisk().stream()
                    .filter(s -> s.getPositionAmt().doubleValue() != 0)
                    .filter(s -> s.getSymbol().equals(cryptoName))
                    .findFirst().orElse(null);
            String stopLossStr = binanceSupport.aroundValueCryptoName(syncRequestClient, cryptoName, String.valueOf(stopLoss));
            String takeProfitStr = binanceSupport.aroundValueCryptoName(syncRequestClient, cryptoName, String.valueOf(takeProfit));
            if (!isNull(positionRisk)) {
                String lot = positionRisk.getPositionAmt().toString().replace("-", "");
                syncRequestClient.postOrder(cryptoName, orderCloseSide, positionSide, OrderType.STOP_MARKET, TimeInForce.GTC,
                        lot, null, null, null, stopLossStr, null, NewOrderRespType.ACK);
                syncRequestClient.postOrder(cryptoName, orderCloseSide, positionSide, OrderType.TAKE_PROFIT_MARKET, TimeInForce.GTC,
                        lot, null, null, null, takeProfitStr, null, NewOrderRespType.ACK);
                return true;
            }
        } catch (Exception e) {
            logger.info(String.format("Error during send SL and Tp to account %s", e));
        }
        return false;
    }

    @Override
    public boolean sendSlToAccount(SyncRequestClient syncRequestClient, String cryptoName, PositionSide positionSide, String stopLoss, String lot) {
        try {
            OrderSide orderCloseSide = getOrderSideClose(positionSide);
            String stopLossStr = binanceSupport.aroundValueCryptoName(syncRequestClient, cryptoName, String.valueOf(stopLoss));
            syncRequestClient.postOrder(cryptoName, orderCloseSide, positionSide, OrderType.STOP_MARKET, TimeInForce.GTC,
                    lot, null, null, null, stopLossStr, null, NewOrderRespType.ACK);
            return true;
        } catch (Exception e) {
            logger.error(String.format("Error during send sl to account %s", e));
        }
        return false;
    }

    @Override
    public boolean sendSlAndTpToAccountMultipleTp(SyncRequestClient syncRequestClient, String cryptoName, PositionSide positionSide, String stopLoss, List<BigDecimal> takeProfit, double minQty, int lever, double marketPrice, OrderType orderType, int lengthPrice, String orderLot) {
        try {
            OrderSide orderCloseSide = getOrderSideClose(positionSide);
            binanceSupport.cancelOpenOrder(syncRequestClient, cryptoName, orderCloseSide, null);
            PositionRisk positionRisk = syncRequestClient.getPositionRisk().stream()
                    .filter(s -> s.getPositionAmt().doubleValue() != 0)
                    .filter(s -> s.getSymbol().equals(cryptoName))
                    .findFirst().orElse(null);
            String stopLossStr = aroundValueCryptoName(null, null, String.valueOf(stopLoss), lengthPrice);
            String lot = OrderType.MARKET.equals(orderType) ? positionRisk.getPositionAmt().toString().replace("-", "") : orderLot;
            List<String> lotsTp = getLotsTp(takeProfit.size(), minQty, Double.parseDouble(lot), lever, marketPrice);
            if (orderType.equals(OrderType.MARKET)) {
                syncRequestClient.postOrder(cryptoName, orderCloseSide, positionSide, OrderType.STOP_MARKET, TimeInForce.GTC,
                        lot, null, null, null, stopLossStr, null, NewOrderRespType.ACK);
            }
            for (int i = 0; i < lotsTp.size(); i++) {
                String takeProfitLot = aroundValueCryptoName(null, null, takeProfit.get(i).toString(), lengthPrice);
                syncRequestClient.postOrder(cryptoName, orderCloseSide, positionSide, OrderType.TAKE_PROFIT_MARKET, TimeInForce.GTC,
                        lotsTp.get(i), null, null, null, takeProfitLot, null, NewOrderRespType.ACK);
            }
            return true;
        } catch (Exception e) {
            logger.info(String.format("Error during cancel order %s", e));
        }
        return false;
    }

    @Override
    public boolean sendOrderToBinance(SyncRequestClient syncRequestClient, String cryptoName, OrderSide orderSide, String lot, String marketPrice, PositionSide positionSide, OrderType orderType, String entryPrice) {
        int count = 0;
        while (true) {
            count++;
            if (count > 2) break;
            try {
                if (OrderType.MARKET.equals(orderType)) {
                    syncRequestClient.postOrder(cryptoName, orderSide, positionSide, orderType, null,
                            lot, null, null, null, null, null, NewOrderRespType.ACK);
                } else if (OrderType.LIMIT.equals(orderType)) {
                    syncRequestClient.postOrder(cryptoName, orderSide, positionSide, orderType, TimeInForce.GTC,
                            lot, entryPrice, null, null, null, null, NewOrderRespType.ACK);
                } else {
                    syncRequestClient.postOrder(cryptoName, orderSide, positionSide, orderType, TimeInForce.GTC,
                            lot, null, null, null, entryPrice, null, NewOrderRespType.ACK);
                }
                return true;
            } catch (Exception e) {
                if (e.toString().contains("position side does not match")) {
                    throw new IllegalArgumentException("position side does not match");
                }
                if (Double.parseDouble(lot) * Double.parseDouble(marketPrice) < 5.0) {
                    throw new IllegalArgumentException("zlecenie poniżej 5$");
                }
                logger.info(e.getMessage());

            }
        }
        return false;
    }

    @Override
    public BinanceConfirmOrder getBinanceConfirmOrder(SyncRequestClient syncRequestClient, pl.coderslab.entity.orders.Order order, double marketPrice) {
        String symbol = order.getSymbolName();
        double sumProfit = 0.0;
        double sumCommission = 0.0;
        Long closeTime = 0L;
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long time = timestamp.getTime();
            long diff = time - 200000;
            List<Income> profitList = syncRequestClient.getIncomeHistory(symbol, IncomeType.REALIZED_PNL, diff, null, 1000);
            List<Income> commissionList = syncRequestClient.getIncomeHistory(symbol, IncomeType.COMMISSION, diff, null, 100);

            if (profitList.isEmpty() || commissionList.isEmpty() || profitList.size() != commissionList.size()) {
                TimeUnit.SECONDS.sleep(2);
                profitList = syncRequestClient.getIncomeHistory(symbol, IncomeType.REALIZED_PNL, diff, null, 50);
                commissionList = syncRequestClient.getIncomeHistory(symbol, IncomeType.COMMISSION, diff, null, 50);
            }
            for (Income income : profitList) {
                closeTime = income.getTime();
                sumProfit += income.getIncome().doubleValue();
            }
            for (Income commission : commissionList) {
                sumCommission += commission.getIncome().doubleValue();
            }
            sumCommission *= 2;
        } catch (InterruptedException e) {
            logger.error(String.format("getBinanceConfirmOrder() %s", e));
        }
        return BinanceConfirmOrder.builder()
                .symbol(symbol)
                .realizedPln(BigDecimal.valueOf(sumProfit).setScale(2, RoundingMode.HALF_UP))
                .commission(BigDecimal.valueOf(sumCommission).setScale(2, RoundingMode.HALF_UP))
                .time(convertTimestampToDate(closeTime))
                .closePrice(String.valueOf(marketPrice))
                .entryPrice(order.getEntry())
                .lot(order.getLot())
                .build();
    }

    @Override
    public void changeStopLoss(SyncRequestClient syncRequestClient, pl.coderslab.entity.orders.Order order, String lot, double slPrice) {
        binanceSupport.cancelOpenOrder(syncRequestClient, order.getSymbolName(), getOrderSideClose(order.getPositionSide()), OrderType.STOP_MARKET);
        sendSlToAccount(syncRequestClient, order.getSymbolName(), order.getPositionSide(), String.valueOf(slPrice), lot);
    }


}
