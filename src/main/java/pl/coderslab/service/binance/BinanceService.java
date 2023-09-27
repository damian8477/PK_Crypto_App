package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.model.Symbol;
import pl.coderslab.repository.SymbolRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BinanceService {

    private final SyncService syncService;
    private final SymbolRepository symbolRepository;

    public List<Symbol> getSymbols(){
         //todo
        return null;
    }
}
