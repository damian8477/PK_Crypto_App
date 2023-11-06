package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.model.market.ExchangeInfoEntry;
import pl.coderslab.interfaces.SyncService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SymbolExchangeService {
    public static List<ExchangeInfoEntry> exchangeInfoEntries;
    private final SyncService syncService;
    @Scheduled(initialDelay = 0, fixedRate = 4 * 60 * 60 * 1000)
    public void downloadExchangeInfoEntries() {
        exchangeInfoEntries = syncService.sync(null).getExchangeInformation().getSymbols();
    }
}
