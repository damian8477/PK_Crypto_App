package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.entity.strategy.StrategySetting;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.enums.Action;
import pl.coderslab.enums.CashType;
import pl.coderslab.enums.MarginType;
import pl.coderslab.interfaces.BinanceUserInterface;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.model.CryptoName;
import pl.coderslab.model.OwnSignal;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class OwnSignalService {

    private final BinanceService binanceService;
    private final BinanceUserInterface binanceSupport;

    public void checkOwnSignal(OwnSignal signal, SyncRequestClient syncRequestClient){
        if(signal.getStrategySetting() == null){
            BigDecimal price = binanceSupport.getMarketPriceBigDecimal(syncRequestClient, signal.getSymbol());

            checkTpAndSl(signal, price);
            checkCashType(signal, price);
            checkOrderSide(signal, price);
            checkTypeOrder(signal, price);
            //todo
        } else {
            //todo
        }
    }

    public  void checkTpAndSl(OwnSignal signal, BigDecimal mp){
        BigDecimal tp = signal.getTakeProfit();
        BigDecimal sl = signal.getStopLoss();
        if(signal.isTpPercent()) {
            if(tp.compareTo(BigDecimal.valueOf(100.0)) > 0 || tp.compareTo(BigDecimal.valueOf(0)) <= 0){
                throw new IllegalArgumentException("% TakeProfit musi się zawierać w przedziale (0; 100>");
            }
        } else {
             BigDecimal percent = ((tp.subtract(mp)).divide(mp, MathContext.DECIMAL32)).multiply(BigDecimal.valueOf(100)).abs();
             if(percent.compareTo(BigDecimal.valueOf(50)) > 0){
                 throw new IllegalArgumentException("TakeProfit musi być poniżej 50%");
             }
        }
        if(signal.isSlPercent()) {
            if(sl.compareTo(BigDecimal.valueOf(100.0)) > 0 || sl.compareTo(BigDecimal.valueOf(0)) <= 0){
                throw new IllegalArgumentException("% TakeProfit musi się zawierać w przedziale (0; 100>");
            }
        } else {
            BigDecimal percent = ((sl.subtract(mp)).divide(mp, MathContext.DECIMAL32)).multiply(BigDecimal.valueOf(100)).abs();
            if(percent.compareTo(BigDecimal.valueOf(50)) > 0){
                throw new IllegalArgumentException("TakeProfit musi być poniżej 50%");
            }
        }
    }
    public void checkCashType(OwnSignal signal, BigDecimal marketPrice){
        CashType cashType = signal.getCashType();
        switch (cashType){
            case LOT -> {

                break;
            }
            case DOLAR -> {

                break;
            }
            case PERCENT -> {

                break;
            }default -> {
                throw new IllegalArgumentException("Wybierz rodzaj płatnosci");
            }
        }
    }
    public void checkOrderSide(OwnSignal signal, BigDecimal marketPrice){
    }
    public void checkTypeOrder(OwnSignal signal, BigDecimal marketPrice){

    }


    public CommonSignal createCommonSignal(User user, OwnSignal signal, StrategySetting strategySetting, UserSetting userSetting, SyncRequestClient syncRequestClient){
        double marketPrice = binanceSupport.getMarketPriceDouble(syncRequestClient, signal.getSymbol());
        return CommonSignal.builder()
                .symbol(signal.getSymbol())
                .positionSide(signal.getPositionSide())
                .entryPrice(signal.getEntryPrice().toString())
                .takeProfit(getTakeProfit(signal.getTakeProfit(), strategySetting))
                .stopLoss(getStopLoss(signal.getStopLoss(), strategySetting))
                .action(Action.OPEN)
                .lot(getLot(user, strategySetting, signal, marketPrice, syncRequestClient))
                .lever(signal.getLever())
                .isStrategy(false)
                .marginType(MarginType.CROSSED)
                .build();
    }

    private String getLot(User user, StrategySetting strategySetting, OwnSignal signal, double marketPrice, SyncRequestClient syncRequestClient){
        switch (signal.getCashType()){
            case LOT -> {
                return signal.getLot().toString();
            }
            case DOLAR ->{
                return binanceSupport.calculateLotSizeQuantityMargin(signal.getSymbol(), signal.getAmount().doubleValue(), signal.getLever(), syncRequestClient, marketPrice).getLot();
            }
            case PERCENT ->{
                double balance = binanceSupport.getUserBalance(syncRequestClient, signal.getSymbol());
                double amount = (signal.getPercentOfAccount().doubleValue() / 100.0) * balance;
                return binanceSupport.calculateLotSizeQuantityMargin(signal.getSymbol(), amount, signal.getLever(), syncRequestClient, marketPrice).getLot();
            }
        }
        throw new IllegalArgumentException("Zły rodzaj płatności (lot, dolar lub %)");
    }

    private List<String> getTakeProfit(BigDecimal tp, StrategySetting strategySetting){
        List<String> takeProfits = new ArrayList<>();
        if(!isNull(strategySetting)){
            if(strategySetting.isActiveBasicTp() && tp == null){
                //takeProfits.add()
            } else {
                takeProfits.add(tp.toString());
            }
        } else {
            takeProfits.add(tp.toString());
        }
        return takeProfits;
    }

    private List<String> getStopLoss(BigDecimal sl, StrategySetting strategySetting){
        List<String> stopLossList = new ArrayList<>();
        if(!isNull(strategySetting)){
            if(strategySetting.isActiveBasicTp() && sl == null){

            } else {
                stopLossList.add(sl.toString());
            }
        } else {
            stopLossList.add(sl.toString());
        }
        return stopLossList;
    }
}

