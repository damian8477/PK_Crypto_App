package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.CandlestickInterval;
import pl.coderslab.binance.client.model.market.Candlestick;
import pl.coderslab.binance.client.model.market.MarkPrice;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.model.CryptoName;
import pl.coderslab.repository.SymbolRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                List<Candlestick> candlestick = syncService.syncRequestClient.getCandlestick(s.getName(), CandlestickInterval.DAILY, null, null, 1);
                if(candlestick.size() > 0){
                    Candlestick cd = candlestick.get(0);
                    cryptoNameList.add(new CryptoName(
                            s.getId(),
                            s.getName(),
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

    public CryptoName getSymbols(int symbolId){
        Symbol symbol = symbolRepository.findById(symbolId).orElse(null);
        if(symbol != null){
            List<Candlestick> candlestick = syncService.syncRequestClient.getCandlestick(symbol.getName(), CandlestickInterval.DAILY, null, null, 1);
            if(candlestick.size() > 0){
                Candlestick cd = candlestick.get(0);
                return new CryptoName(
                        symbol.getId(),
                        symbol.getName(),
                        cd.getClose(),
                        false, //todo
                        cd.getLow(),
                        cd.getHigh(),
                        ((cd.getClose().subtract(cd.getOpen())).multiply(BigDecimal.valueOf(100.0)).divide(cd.getClose(), 2, RoundingMode.HALF_UP))
                );
            }
        }
        return new CryptoName();
    }

    public List<String> getSymbolNames(){
        return syncService.syncRequestClient.getPositionRisk().stream()
                .map(PositionRisk::getSymbol)
                .toList();
    }

    public BigDecimal getMarketPrice(String cryptoName) {
        List<MarkPrice> marketPrices = syncService.syncRequestClient.getMarkPrice(cryptoName);
        return marketPrices.get(0).getMarkPrice();
    }
}
