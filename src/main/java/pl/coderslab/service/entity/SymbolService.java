package pl.coderslab.service.entity;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.service.binance.BinanceService;

@Service
@RequiredArgsConstructor
public class SymbolService {
    private final BinanceService binanceService;

    public Symbol checkSymbol(Symbol symbol){
        symbol.setName(symbol.getName().toUpperCase());
        if(binanceService.getSymbols().stream().anyMatch(s -> s.getSymbol().equals(symbol.getName()))){
            throw new IllegalArgumentException("Symbol znajduje się już na liście");
        }
        if(!binanceService.getSymbolNames().contains(symbol.getName())){
            throw new IllegalArgumentException("Nie ma takiego symbolu na giełdzie binance");
        }
        return symbol;
    }
}
