package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.enums.CashType;
import pl.coderslab.model.CryptoName;
import pl.coderslab.model.OwnSignal;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OwnSignalService {

    private final BinanceService binanceService;

    public void checkOwnSignal(OwnSignal signal){
        if(signal.getStrategySetting() == null){
            BigDecimal price = binanceService.getMarketPrice(signal.getSymbol());

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
             BigDecimal percent = ((tp.subtract(mp)).divide(mp)).multiply(BigDecimal.valueOf(100)).abs();
             if(percent.compareTo(BigDecimal.valueOf(50)) > 0){
                 throw new IllegalArgumentException("TakeProfit musi być poniżej 50%");
             }
        }
        if(signal.isSlPercent()) {
            if(sl.compareTo(BigDecimal.valueOf(100.0)) > 0 || sl.compareTo(BigDecimal.valueOf(0)) <= 0){
                throw new IllegalArgumentException("% TakeProfit musi się zawierać w przedziale (0; 100>");
            }
        } else {
            BigDecimal percent = ((sl.subtract(mp)).divide(mp)).multiply(BigDecimal.valueOf(100)).abs();
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
}

