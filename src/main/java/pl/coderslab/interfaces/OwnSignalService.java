package pl.coderslab.interfaces;

import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.model.OwnSignal;

import java.math.BigDecimal;
import java.util.List;

public interface OwnSignalService {
    void checkOwnSignal(OwnSignal signal, SyncRequestClient syncRequestClient);

    void checkTpAndSl(OwnSignal signal, BigDecimal mp);

    void checkCashType(OwnSignal signal, BigDecimal marketPrice);

    void checkOrderSide(OwnSignal signal, BigDecimal marketPrice);

    void checkTypeOrder(OwnSignal signal, BigDecimal marketPrice);

    CommonSignal createCommonSignal(User user, OwnSignal signal, Strategy strategySetting, UserSetting userSetting, SyncRequestClient syncRequestClient);

    String getLot(User user, Source strategySetting, OwnSignal signal, double marketPrice, SyncRequestClient syncRequestClient);

    List<BigDecimal> getTakeProfit(BigDecimal tp, Source strategySetting);

    List<BigDecimal> getStopLoss(BigDecimal sl, Source strategySetting);
}
