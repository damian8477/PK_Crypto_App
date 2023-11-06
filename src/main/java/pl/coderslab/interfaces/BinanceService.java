package pl.coderslab.interfaces;

import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.OrderSide;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.model.BinanceConfirmOrder;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.model.CryptoName;

import java.math.BigDecimal;
import java.util.List;

public interface BinanceService {
    List<CryptoName> getSymbols();

    CryptoName getSymbols(int symbolId);

    List<String> getSymbolNames();

    boolean createOrder(CommonSignal signal, User user, SyncRequestClient syncRequestClient);

    SyncRequestClient sync(UserSetting userSetting);

    boolean sendSlAndTpToAccount(SyncRequestClient syncRequestClient, String cryptoName, OrderSide orderSide, PositionSide positionSide, String stopLoss, String takeProfit);

    boolean sendSlAndTpToAccountMultipleTp(SyncRequestClient syncRequestClient, String cryptoName, PositionSide positionSide, String stopLoss, List<BigDecimal> takeProfit, double minQty, int lever, double marketPrice, OrderType orderType, int lengthPrice);

    boolean sendOrderToBinance(SyncRequestClient syncRequestClient, String cryptoName, OrderSide orderSide, String lot, String marketPrice, PositionSide positionSide, OrderType orderType);

    void cancelAllOpenOrders(SyncRequestClient syncRequestClient, String symbol, String side);

    BinanceConfirmOrder getBinanceConfirmOrder(SyncRequestClient syncRequestClient, PositionRisk positionRisk);

    String convertTimestampToDate(Long timestamp);
}
