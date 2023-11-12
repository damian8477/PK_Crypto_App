package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.*;
import pl.coderslab.binance.client.model.market.Candlestick;
import pl.coderslab.binance.client.model.trade.Income;
import pl.coderslab.binance.client.model.trade.Order;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BinanceServiceImpl extends Common implements BinanceService {

    private final SyncService syncService;
    private final SymbolRepository symbolRepository;
    private final BinanceBasicService binanceSupport;
    private final OrderService orderService;
    private final TelegramBotService telegramBotService;

    private static final Logger logger = LoggerFactory.getLogger(BinanceServiceImpl.class);
    @Override
    public List<CryptoName> getSymbols() {
        List<CryptoName> cryptoNameList = new ArrayList<>();
        List<Symbol> symbolList = symbolRepository.findAll();
        SyncRequestClient syncRequestClient = syncService.sync(null);
        if (symbolList != null) {
            symbolList.forEach(s -> {
                List<Candlestick> candlestick = syncRequestClient.getCandlestick(s.getName(), CandlestickInterval.DAILY, null, null, 1);
                if (candlestick.size() > 0) {
                    Candlestick cd = candlestick.get(0);
                    cryptoNameList.add(new CryptoName(
                            s.getId(),
                            s.getName(),
                            cd.getClose(),
                            false, //todo
                            cd.getLow(),
                            cd.getHigh(),
                            ((cd.getClose().subtract(cd.getOpen())).multiply(BigDecimal.valueOf(100.0)).divide(cd.getClose(), 2, RoundingMode.HALF_UP))
                    ));
                }
            });
        }
        return cryptoNameList;
    }
    @Override
    public CryptoName getSymbols(int symbolId) {
        Symbol symbol = symbolRepository.findById(symbolId).orElse(null);
        if (symbol != null) {
            List<Candlestick> candlestick = syncService.sync(null).getCandlestick(symbol.getName(), CandlestickInterval.DAILY, null, null, 1);
            if (candlestick.size() > 0) {
                Candlestick cd = candlestick.get(0);
                return new CryptoName(
                        symbol.getId(),
                        symbol.getName(),
                        cd.getClose(),
                        false, //todo
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
        //todo sprawdzenie czy sygnal jest otwarty
        OrderSide orderSide = binanceSupport.getOrderSideForOpen(signal.getPositionSide());
        PositionSide positionSide = signal.getPositionSide();
        binanceSupport.setMarginType(syncRequestClient, signal.getMarginType(), signal.getSymbol());
        int lever = binanceSupport.setLeverage(syncRequestClient, signal.getLever(), signal.getSymbol());
        String marketPrice = binanceSupport.getMarketPriceString(syncRequestClient, signal.getSymbol());
        String lot = signal.getLot();
        boolean isOpen = false;
        if (signal.getOrderType().equals(OrderType.MARKET)) isOpen = true;
        if (sendOrderToBinance(syncRequestClient, signal.getSymbol(), orderSide, lot, marketPrice, positionSide, signal.getOrderType(), null)) {
            if (isOpen) {
                sendSlAndTpToAccount(syncRequestClient, signal.getSymbol(), orderSide, positionSide, signal.getStopLoss().get(0).toString(), signal.getTakeProfit().get(0).toString());
            }
            orderService.save(user, signal, marketPrice, lot, "", "", lever, null, isOpen, null);
            telegramBotService.sendMessage(user.getUserSetting().get(0).getTelegramChatId(), String.format("%s Zlecenie otwarte! \n%s %s $%s LOT: $%s", Emoticon.OPEN.getLabel(), signal.getSymbol(), Emoticon.valueOf(signal.getPositionSide().toString()), marketPrice, signal.getLot()));
            logger.info(String.format("Username: %s\n%s Zlecenie otwarte! \n%s %s $%s LOT: $%s", user.getUsername(), Emoticon.OPEN.getLabel(), signal.getSymbol(), Emoticon.valueOf(signal.getPositionSide().toString()), marketPrice, signal.getLot()));
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
            OrderSide orderCloseSide = OrderSide.BUY;
            if (orderSide.equals(OrderSide.BUY)) orderCloseSide = OrderSide.SELL;
            String finalSide = orderCloseSide.toString();
            try {
                List<Order> listOrder = syncRequestClient.getOpenOrders(cryptoName).stream()
                        .filter(s -> s.getSide().equals(finalSide))
                        .toList();
                for (Order order : listOrder) {
                    syncRequestClient.cancelOrder(cryptoName, order.getOrderId(), order.getClientOrderId());
                }
            } catch (Exception e) {

            }
            PositionRisk positionRisk = syncRequestClient.getPositionRisk().stream()
                    .filter(s -> s.getPositionAmt().doubleValue() != 0)
                    .filter(s -> s.getSymbol().equals(cryptoName))
                    .findFirst().orElse(null);
            String stopLossStr = binanceSupport.aroundValueCryptoName(syncRequestClient, cryptoName, String.valueOf(stopLoss));
            String takeProfitStr = binanceSupport.aroundValueCryptoName(syncRequestClient, cryptoName, String.valueOf(takeProfit));
            String lot = positionRisk.getPositionAmt().toString().replace("-", "");
            syncRequestClient.postOrder(cryptoName, orderCloseSide, positionSide, OrderType.STOP_MARKET, TimeInForce.GTC,
                    lot, null, null, null, stopLossStr, null, NewOrderRespType.ACK);
            syncRequestClient.postOrder(cryptoName, orderCloseSide, positionSide, OrderType.TAKE_PROFIT_MARKET, TimeInForce.GTC,
                    lot, null, null, null, takeProfitStr, null, NewOrderRespType.ACK);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean sendSlToAccount(SyncRequestClient syncRequestClient, String cryptoName, PositionSide positionSide, String stopLoss, String lot) {
        try {
            OrderSide orderCloseSide = OrderSide.BUY;
            if (positionSide.equals(PositionSide.LONG)) orderCloseSide = OrderSide.SELL;
            String stopLossStr = binanceSupport.aroundValueCryptoName(syncRequestClient, cryptoName, String.valueOf(stopLoss));
            syncRequestClient.postOrder(cryptoName, orderCloseSide, positionSide, OrderType.STOP_MARKET, TimeInForce.GTC,
                    lot, null, null, null, stopLossStr, null, NewOrderRespType.ACK);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean sendSlAndTpToAccountMultipleTp(SyncRequestClient syncRequestClient, String cryptoName, PositionSide positionSide, String stopLoss, List<BigDecimal> takeProfit, double minQty, int lever, double marketPrice, OrderType orderType, int lengthPrice, String orderLot) {
        try {
            OrderSide orderCloseSide;
            if (positionSide.equals(PositionSide.LONG)) orderCloseSide = OrderSide.SELL;
            else {
                orderCloseSide = OrderSide.BUY;
            }
            String finalSide = orderCloseSide.toString();
            try {
                List<Order> listOrder = syncRequestClient.getOpenOrders(cryptoName).stream()
                        .filter(s -> s.getSide().equals(finalSide))
                        .toList();
                for (Order order : listOrder) {
                    syncRequestClient.cancelOrder(cryptoName, order.getOrderId(), order.getClientOrderId());
                }
            } catch (Exception e) {
                logger.info("Error during cancel order");
            }
            PositionRisk positionRisk = syncRequestClient.getPositionRisk().stream()
                    .filter(s -> s.getPositionAmt().doubleValue() != 0)
                    .filter(s -> s.getSymbol().equals(cryptoName))
                    .findFirst().orElse(null);
            String stopLossStr = aroundValueCryptoName(null, null, String.valueOf(stopLoss), lengthPrice);
            String lot = OrderType.MARKET.equals(orderType) ? positionRisk.getPositionAmt().toString().replace("-", "") : orderLot;
            List<String> lotsTp = getLotsTp(takeProfit.size(), minQty, Double.parseDouble(lot), lever, marketPrice);
            if(orderType.equals(OrderType.MARKET)){
                syncRequestClient.postOrder(cryptoName, orderCloseSide, positionSide, OrderType.STOP_MARKET, TimeInForce.GTC,
                        lot, null, null, null, stopLossStr, null, NewOrderRespType.ACK);
            }
            for (int i = 0; i < lotsTp.size(); i++) {
                String takeProfitLot = aroundValueCryptoName(null, null, takeProfit.get(i).toString(), lengthPrice);
                syncRequestClient.postOrder(cryptoName, orderCloseSide, positionSide, OrderType.TAKE_PROFIT_MARKET, TimeInForce.GTC,
                        lotsTp.get(i), null, null, null, takeProfitLot, null, NewOrderRespType.ACK);
            }
        } catch (Exception e) {
            logger.info("Error during cancel order " + e);
            return false;
        }
        return true;
    }

    @Override
    public boolean sendOrderToBinance(SyncRequestClient syncRequestClient, String cryptoName, OrderSide orderSide, String lot, String marketPrice, PositionSide positionSide, OrderType orderType, String entryPrice) {
        int count = 0;
        while (true) {
            count++;
            if (count > 2) break;
            try {
                if(OrderType.MARKET.equals(orderType)){
                    syncRequestClient.postOrder(cryptoName, orderSide, positionSide, orderType, null,
                            lot, null, null, cryptoName, null, null, NewOrderRespType.ACK);
                } else {
                    syncRequestClient.postOrder(cryptoName, orderSide, positionSide, orderType,  TimeInForce.GTC,
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
            }
        }
        syncRequestClient.postOrder(cryptoName, orderSide, PositionSide.BOTH, orderType, null,
                lot, null, null, null, null, null, NewOrderRespType.ACK);
        return true;
    }
    @Override
    public void cancelAllOpenOrders(SyncRequestClient syncRequestClient, String symbol, String side) {
        String sideCancelFinal = side;
        try {
            List<Order> listOrder = syncRequestClient.getOpenOrders(symbol).stream()
                    .filter(s -> s.getSide().equals(sideCancelFinal))
                    .toList();
            for (Order order : listOrder) {
                syncRequestClient.cancelOrder(symbol, order.getOrderId(), order.getClientOrderId());
            }
        } catch (Exception e) {
            syncRequestClient.cancelAllOpenOrder(symbol);
        }
    }
    @Override
    public BinanceConfirmOrder getBinanceConfirmOrder(SyncRequestClient syncRequestClient, PositionRisk positionRisk) {
        String symbol = positionRisk.getSymbol();
        double sumProfit = 0.0;
        double sumCommission = 0.0;
        Long closeTime = 0l;
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long time = timestamp.getTime();
            long diff = time - 20000;
            List<Income> profitList = syncRequestClient.getIncomeHistory(symbol, IncomeType.REALIZED_PNL, diff, null, 50);
            List<Income> commissionList = syncRequestClient.getIncomeHistory(symbol, IncomeType.COMMISSION, diff, null, 50);

            if (profitList.isEmpty() || commissionList.isEmpty() || profitList.size() != commissionList.size()) {
                TimeUnit.SECONDS.sleep(2);
                profitList = syncRequestClient.getIncomeHistory(symbol, IncomeType.REALIZED_PNL, diff, null, 50);
                commissionList = syncRequestClient.getIncomeHistory(symbol, IncomeType.COMMISSION, diff, null, 50);
            }
            for (Income income : profitList) {
                closeTime = income.getTime(); //todo pobrac czas z income, ale trzeba przekonwertowac timestamp na time
                sumProfit += income.getIncome().doubleValue();
            }
            for (Income commission : commissionList) {
                sumCommission += commission.getIncome().doubleValue();
            }
            sumCommission *= 2;
        } catch (Exception e) {
            logger.error(String.valueOf(e));
        }
        return BinanceConfirmOrder.builder()
                .symbol(symbol)
                .realizedPln(BigDecimal.valueOf(sumProfit).setScale(2, RoundingMode.HALF_UP))
                .commission(BigDecimal.valueOf(sumCommission).setScale(2, RoundingMode.HALF_UP))
                .time(convertTimestampToDate(closeTime))
                .closePrice(positionRisk.getMarkPrice().toString())
                .entryPrice(positionRisk.getEntryPrice().toString())
                .lot(positionRisk.getPositionAmt().abs().toString())
                .build();
    }

    @Override
    public String convertTimestampToDate(Long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

}
