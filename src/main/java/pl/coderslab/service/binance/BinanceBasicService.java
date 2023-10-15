package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.CandlestickInterval;
import pl.coderslab.binance.client.model.enums.OrderSide;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.market.Candlestick;
import pl.coderslab.binance.client.model.market.ExchangeInfoEntry;
import pl.coderslab.binance.client.model.market.ExchangeInformation;
import pl.coderslab.binance.client.model.market.MarkPrice;
import pl.coderslab.binance.client.model.trade.AccountBalance;
import pl.coderslab.binance.client.model.trade.MarginLot;
import pl.coderslab.binance.client.model.trade.Order;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.MarginType;
import pl.coderslab.interfaces.BinanceUserInterface;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Double.parseDouble;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class BinanceBasicService implements BinanceUserInterface {

    private final SyncService syncService;
    private static final Logger logger = LoggerFactory.getLogger(BinanceBasicService.class);


    public void setMarginType(SyncRequestClient syncRequestClient, MarginType marginType, String cryptoName) {
        try {
            syncRequestClient.changeMarginType(cryptoName, marginType.toString());
        } catch (Exception ignored) {
        }
    }

    @Override
    public OrderSide getOrderSideForOpen(PositionSide positionSide) {
        if (positionSide.equals(PositionSide.LONG)) {
            return OrderSide.BUY;
        } else {
            return OrderSide.SELL;
        }
    }

    @Override
    public OrderSide getOrderSideForClose(PositionSide positionSide) {
        if (positionSide.equals(PositionSide.LONG)) {
            return OrderSide.SELL;
        } else {
            return OrderSide.BUY;
        }
    }

    @Override
    public int setLeverage(SyncRequestClient syncRequestClient, int leverage, String symbol) {
        try {
            syncRequestClient.changeInitialLeverage(symbol, leverage);
            return leverage;
        } catch (Exception e) {
            int lever = leverage - 1;
            if (lever > 0)
                return setLeverage(syncRequestClient, lever, symbol);
        }
        return leverage;
    }

    @Override
    public String getMarketPriceString(SyncRequestClient syncRequestClient, String symbol) {
        if (isNull(syncRequestClient)) {
            syncRequestClient = syncService.sync(null);
        }
        List<MarkPrice> marketPrices = syncRequestClient.getMarkPrice(symbol);
        return marketPrices.get(0).getMarkPrice().toString();
    }

    @Override
    public String getMarketPriceString(User user, String symbol) {
        SyncRequestClient syncRequestClient = syncService.sync(user.getUserSetting().get(0));
        if (isNull(syncRequestClient)) {
            syncRequestClient = syncService.sync(null);
        }
        List<MarkPrice> marketPrices = syncRequestClient.getMarkPrice(symbol);
        return marketPrices.get(0).getMarkPrice().toString();
    }

    @Override
    public double getMarketPriceDouble(SyncRequestClient syncRequestClient, String symbol) {
        if (isNull(syncRequestClient)) {
            syncRequestClient = syncService.sync(null);
        }
        List<MarkPrice> marketPrices = syncRequestClient.getMarkPrice(symbol);
        return marketPrices.get(0).getMarkPrice().doubleValue();
    }

    @Override
    public BigDecimal getMarketPriceBigDecimal(SyncRequestClient syncRequestClient, String symbol) {
        if (isNull(syncRequestClient)) {
            syncRequestClient = syncService.sync(null);
        }
        List<MarkPrice> marketPrices = syncRequestClient.getMarkPrice(symbol);
        return marketPrices.get(0).getMarkPrice();
    }

    @Override
    public String aroundValueCryptoName(SyncRequestClient syncRequestClient, String cryptoName, String div) {
        List<Candlestick> candlestickList = syncRequestClient.getCandlestick(cryptoName, CandlestickInterval.FIVE_MINUTES, null, null, 2);
        int lenghEnter = candlestickList.get(0).getClose().toString().length();
        if (candlestickList.get(0).getOpen().toString().length() > lenghEnter)
            lenghEnter = candlestickList.get(0).getOpen().toString().length();
        if (candlestickList.get(0).getHigh().toString().length() > lenghEnter)
            lenghEnter = candlestickList.get(0).getHigh().toString().length();
        if (candlestickList.get(0).getLow().toString().length() > lenghEnter)
            lenghEnter = candlestickList.get(0).getLow().toString().length();
        if (String.valueOf(div).length() > lenghEnter) {
            return String.valueOf(div).substring(0, lenghEnter);
        }
        return div;
    }

    @Override
    public void cancelOpenOrder(SyncRequestClient syncRequestClient, String symbol, OrderSide orderSide) {
        try {
            String side = orderSide.toString();
            List<Order> listOrder = syncRequestClient.getOpenOrders(symbol).stream()
                    .filter(s -> s.getSide().equals(side))
                    .toList();
            for (Order order : listOrder) {
                syncRequestClient.cancelOrder(symbol, order.getOrderId(), order.getClientOrderId());
            }
        } catch (Exception e) {
            //todo
        }
    }

    @Override
    public MarginLot calculateLotSizeQuantityMargin(String symbol, double amountInUSDT, int leverage, SyncRequestClient syncRequestClient, double marketPrice) {
        try {
            double minLotSize = calculateMinimumLotSize(symbol, syncRequestClient);
            double margin = amountInUSDT * (double) leverage;
            double lotSize = margin / marketPrice;
            return new MarginLot("0.0", Double.toString(calculateLot(lotSize, minLotSize)));
        } catch (Exception e) {
            return new MarginLot("0.0", "0.0");
        }
    }

    @Override
    public double calculateMinimumLotSize(String symbol, SyncRequestClient syncRequestClient) {
        ExchangeInformation exchangeInformation = syncRequestClient.getExchangeInformation();
        List<ExchangeInfoEntry> exchangeInfoEntries = exchangeInformation.getSymbols();

        Optional<Map.Entry<String, String>> minQty = exchangeInfoEntries.stream()
                .filter(obj -> obj.getSymbol().equals(symbol))
                .map(ExchangeInfoEntry::getFilters)
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .flatMap(s -> s.entrySet().stream())
                .filter(s -> s.getKey().equals("minQty"))
                .findFirst();

        if (minQty.isPresent()) {
            Map.Entry<String, String> value = minQty.get();
            if (parseDouble(value.getValue()) == 0.001) return 1000.0;
            if (parseDouble(value.getValue()) == 0.01) return 100.0;
            if (parseDouble(value.getValue()) == 0.1) return 10.0;
            if (parseDouble(value.getValue()) == 1) return 1.0;
        }
        return 0.0;
    }

    @Override
    public double calculateLot(double lotSize, double minLotSize) {
        return Math.round(Math.abs(lotSize) * minLotSize) / minLotSize;
    }

    @Override
    public double getUserBalance(SyncRequestClient syncRequestClient, String cryptoName) {
        String curr = getCurrency(cryptoName);
        List<AccountBalance> balanceUserList = syncRequestClient.getBalance();
        List<BigDecimal> balanceList = balanceUserList.stream()
                .filter((s -> s.getAsset().equals(curr)))
                .map(AccountBalance::getBalance).toList();
        return Math.round(balanceList.get(0).doubleValue() * 100.0) / 100.0;
    }

    @Override
    public String getCurrency(String cryptoName) {
        if (cryptoName.contains("USDT")) {
            return "USDT";
        } else if (cryptoName.contains("BUSD")) {
            return "BUSD";
        }
        return "USDT";
    }
}
