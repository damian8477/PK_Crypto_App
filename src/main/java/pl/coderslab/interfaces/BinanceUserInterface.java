package pl.coderslab.interfaces;

import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.OrderSide;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.trade.MarginLot;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.MarginType;

import java.math.BigDecimal;
import java.util.Optional;

public interface BinanceUserInterface {
    void setMarginType(SyncRequestClient syncRequestClient, MarginType marginType, String symbol);
    OrderSide getOrderSideForOpen(PositionSide orderSide);
    OrderSide getOrderSideForClose(PositionSide orderSide, double positionAmount);
    int setLeverage(SyncRequestClient syncRequestClient, int leverage, String symbol);
    String getMarketPriceString(SyncRequestClient syncRequestClient, String symbol);

    double getMarketPriceDouble(SyncRequestClient syncRequestClient, String symbol);

    BigDecimal getMarketPriceBigDecimal(SyncRequestClient syncRequestClient, String symbol);

    String aroundValueCryptoName(SyncRequestClient syncRequestClient, String cryptoName, String div);

    void cancelOpenOrder(SyncRequestClient syncRequestClient, String symbol, OrderSide orderSide);

    MarginLot calculateLotSizeQuantityMargin(String symbol, double amountInUSDT, int leverage, SyncRequestClient syncRequestClient, double marketPrice);

    double calculateMinimumLotSize(String symbol, SyncRequestClient syncRequestClient);

    double calculateLot(double lotSize, double minLotSize);


    double getUserBalance(SyncRequestClient syncRequestClient, String cryptoName);

    String getCurrency(String cryptoName);
}
