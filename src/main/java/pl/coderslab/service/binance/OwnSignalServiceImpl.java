package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.common.Common;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.enums.Action;
import pl.coderslab.enums.CashType;
import pl.coderslab.enums.MarginType;
import pl.coderslab.interfaces.BinanceBasicService;
import pl.coderslab.interfaces.OwnSignalService;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.model.OwnSignal;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class OwnSignalServiceImpl implements OwnSignalService, Common {
    private final BinanceBasicService binanceSupport;
    private static final Logger logger = LoggerFactory.getLogger(OwnSignalServiceImpl.class);

    @Override
    public void checkOwnSignal(OwnSignal signal, SyncRequestClient syncRequestClient) {
        if (signal.getStrategySetting() == null) {
            BigDecimal price = binanceSupport.getMarketPriceBigDecimal(syncRequestClient, signal.getSymbol());
            checkTpAndSl(signal, price);
            checkCashType(signal, price);
            checkOrderSide(signal, price);
            checkTypeOrder(signal, price);
        }
    }

    @Override
    public void checkTpAndSl(OwnSignal signal, BigDecimal mp) {
        BigDecimal tp = signal.getTakeProfit();
        BigDecimal sl = signal.getStopLoss();
        if (signal.isTpPercent()) {
            if (tp.compareTo(BigDecimal.valueOf(100.0)) > 0 || tp.compareTo(BigDecimal.valueOf(0)) <= 0) {
                throw new IllegalArgumentException("% TakeProfit musi się zawierać w przedziale (0; 100>");
            }
        } else {
            BigDecimal percent = ((tp.subtract(mp)).divide(mp, MathContext.DECIMAL32)).multiply(BigDecimal.valueOf(100)).abs();
            if (percent.compareTo(BigDecimal.valueOf(50)) > 0) {
                throw new IllegalArgumentException("TakeProfit musi być poniżej 50%");
            }
        }
        if (signal.isSlPercent()) {
            if (sl.compareTo(BigDecimal.valueOf(100.0)) > 0 || sl.compareTo(BigDecimal.valueOf(0)) <= 0) {
                throw new IllegalArgumentException("% TakeProfit musi się zawierać w przedziale (0; 100>");
            }
        } else {
            BigDecimal percent = ((sl.subtract(mp)).divide(mp, MathContext.DECIMAL32)).multiply(BigDecimal.valueOf(100)).abs();
            if (percent.compareTo(BigDecimal.valueOf(50)) > 0) {
                throw new IllegalArgumentException("TakeProfit musi być poniżej 50%");
            }
        }
    }

    @Override
    public void checkCashType(OwnSignal signal, BigDecimal marketPrice) {
        CashType cashType = signal.getCashType();
        switch (cashType) {
            case LOT -> {

                break;
            }
            case DOLAR -> {

                break;
            }
            case PERCENT -> {

                break;
            }
            default -> {
                throw new IllegalArgumentException("Wybierz rodzaj płatnosci");
            }
        }
    }

    @Override
    public void checkOrderSide(OwnSignal signal, BigDecimal marketPrice) {
    }

    @Override
    public void checkTypeOrder(OwnSignal signal, BigDecimal marketPrice) {

    }

    @Override
    public CommonSignal createCommonSignal(User user, OwnSignal signal, Strategy strategySetting, UserSetting userSetting, SyncRequestClient syncRequestClient) {
        double marketPrice = binanceSupport.getMarketPriceDouble(syncRequestClient, signal.getSymbol());
        return CommonSignal.builder()
                .symbol(signal.getSymbol())
                .positionSide(signal.getPositionSide())
                .entryPrice(List.of(signal.getEntryPrice()))
                .takeProfit(getTakeProfit(signal.getTakeProfit(), null))
                .stopLoss(getStopLoss(signal.getStopLoss(), null))
                .action(Action.OPEN)
                .lot(getLot(user, null, signal, marketPrice, syncRequestClient))
                .lever(signal.getLever())
                .isStrategy(false)
                .marginType(MarginType.CROSSED)
                .orderType(signal.getTypeOrder())
                .build();
    }

    @Override
    public String getLot(User user, Source strategySetting, OwnSignal signal, double marketPrice, SyncRequestClient syncRequestClient) {
        switch (signal.getCashType()) {
            case LOT -> {
                return signal.getLot().toString();
            }
            case DOLAR -> {
                return binanceSupport.calculateLotSizeQuantityMargin(signal.getSymbol(), signal.getAmount().doubleValue(), signal.getLever(), syncRequestClient, marketPrice).getLot();
            }
            case PERCENT -> {
                double balance = getUserBalanceDouble(syncRequestClient, signal.getSymbol());
                double amount = (signal.getPercentOfAccount().doubleValue() / 100.0) * balance;
                return binanceSupport.calculateLotSizeQuantityMargin(signal.getSymbol(), amount, signal.getLever(), syncRequestClient, marketPrice).getLot();
            }
        }
        throw new IllegalArgumentException("Zły rodzaj płatności (lot, dolar lub %)");
    }

    @Override
    public List<BigDecimal> getTakeProfit(BigDecimal tp, Source strategySetting) {
        List<BigDecimal> takeProfits = new ArrayList<>();
        if (!isNull(strategySetting)) {
            if (!(strategySetting.isActiveBasicTp() && tp == null)) {
                takeProfits.add(tp);
            }
        } else {
            takeProfits.add(tp);
        }
        return takeProfits;
    }

    @Override
    public List<BigDecimal> getStopLoss(BigDecimal sl, Source strategySetting) {
        List<BigDecimal> stopLossList = new ArrayList<>();
        if (!isNull(strategySetting)) {
            if (!(strategySetting.isActiveBasicTp() && sl == null)) {
                stopLossList.add(sl);
            }
        } else {
            stopLossList.add(sl);
        }
        return stopLossList;
    }
}

