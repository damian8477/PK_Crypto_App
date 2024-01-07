package pl.coderslab.interfaces;

import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.model.AlertSetting;

import java.util.List;

public interface SymbolService {
    Symbol findById(Integer symbolId);

    void checkSymbol(Symbol symbol);

    AlertSetting getBasicAlert(String symbol);
    List<Symbol> findAll();
}
