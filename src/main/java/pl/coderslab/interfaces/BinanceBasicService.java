package pl.coderslab.interfaces;

import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.OrderSide;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.trade.MarginLot;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.MarginType;
import pl.coderslab.model.BinanceConfirmOrder;

import java.math.BigDecimal;
import java.util.List;

public interface BinanceBasicService {
    void setMarginType(SyncRequestClient syncRequestClient, MarginType marginType, String symbol);

    OrderSide getOrderSideForOpen(PositionSide orderSide);

    OrderSide getOrderSideForClose(PositionSide positionSide);

    int setLeverage(SyncRequestClient syncRequestClient, int leverage, String symbol);

    String getMarketPriceString(SyncRequestClient syncRequestClient, String symbol);

    String getMarketPriceString(User user, String symbol);

    double getMarketPriceDouble(SyncRequestClient syncRequestClient, String symbol);

    BigDecimal getMarketPriceBigDecimal(SyncRequestClient syncRequestClient, String symbol);

    String aroundValueCryptoName(SyncRequestClient syncRequestClient, String cryptoName, String div);

    void cancelOpenOrder(SyncRequestClient syncRequestClient, String symbol, OrderSide orderSide, OrderType orderType);

    MarginLot calculateLotSizeQuantityMargin(String symbol, double amountInUSDT, int leverage, SyncRequestClient syncRequestClient, double marketPrice);

    double calculateMinimumLotSize(String symbol, SyncRequestClient syncRequestClient);

    double calculateLot(double lotSize, double minLotSize);


    String convertTimestampToDate(Long timestamp);

    BinanceConfirmOrder getBinanceConfirmOrder(SyncRequestClient syncRequestClient, PositionRisk positionRisk);

    List<String> getSymbolList();
}
