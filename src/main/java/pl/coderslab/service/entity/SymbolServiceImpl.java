package pl.coderslab.service.entity;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.interfaces.BinanceBasicService;
import pl.coderslab.interfaces.BinanceService;
import pl.coderslab.interfaces.SymbolService;
import pl.coderslab.model.AlertSetting;
import pl.coderslab.repository.SymbolRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SymbolServiceImpl implements SymbolService {
    private final BinanceService binanceService;
    private final BinanceBasicService binanceUserService;
    private final SymbolRepository symbolRepository;
    private static final Logger logger = LoggerFactory.getLogger(SymbolServiceImpl.class);

    @Override
    public Symbol findById(Integer symbolId) {
        return symbolRepository.findById(symbolId).orElse(null);
    }

    @Override
    public boolean checkSymbol(Symbol symbol) {
        symbol.setName(symbol.getName().toUpperCase());
        if (binanceService.getSymbols().stream().anyMatch(s -> s.getSymbol().equals(symbol.getName()))) {
//            throw new IllegalArgumentException("Symbol znajduje się już na liście");
            return false;
        }
        if (!binanceService.getSymbolNames().contains(symbol.getName())) {
            throw new IllegalArgumentException("Nie ma takiego symbolu na giełdzie binance");
        }
        return true;
    }

    @Override
    public AlertSetting getBasicAlert(String symbol) {
        return AlertSetting.builder()
                .symbol(symbol)
                .marketPrice(binanceUserService.getMarketPriceBigDecimal(null, symbol))
                .build();
    }

    @Override
    public List<Symbol> findAll() {
        return symbolRepository.findAll();
    }
}
