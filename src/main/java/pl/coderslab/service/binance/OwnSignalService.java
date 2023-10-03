package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
//            checkTpAndSl(signal);
//            checkCashType(signal);
//            checkOrderSide(signal);
//            checkTypeOrder(signal);
            //todo
        } else {
            //todo
        }
    }
}
