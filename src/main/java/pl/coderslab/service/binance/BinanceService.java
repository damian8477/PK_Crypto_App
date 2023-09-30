package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.model.enums.CandlestickInterval;
import pl.coderslab.binance.client.model.market.Candlestick;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.model.CryptoName;
import pl.coderslab.repository.SymbolRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BinanceService {

    private final SyncService syncService;
    private final SymbolRepository symbolRepository;


    public List<CryptoName> getSymbols(){
        List<CryptoName> cryptoNameList = new ArrayList<>();
        List<Symbol> symbolList = symbolRepository.findAll();
        if(symbolList != null){
            symbolList.forEach(s -> {
                List<Candlestick> candlestick = syncService.syncRequestClient.getCandlestick(s.getSymbol(), CandlestickInterval.DAILY, null, null, 1);
                if(candlestick.size() > 0){
                    Candlestick cd = candlestick.get(0);
                    cryptoNameList.add(new CryptoName(
                            s.getId(),
                            s.getSymbol(),
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
}
