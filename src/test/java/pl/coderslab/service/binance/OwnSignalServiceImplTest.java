package pl.coderslab.service.binance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.enums.CashType;
import pl.coderslab.interfaces.BinanceBasicService;
import pl.coderslab.model.OwnSignal;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OwnSignalServiceImplTest {
    @Mock
    private BinanceBasicService binanceSupport;

    @InjectMocks
    private OwnSignalServiceImpl ownSignalService;

    @Mock
    private SyncRequestClient syncRequestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkOwnSignal_WithNullStrategySetting_ShouldCheckTpAndSl() {
        // Arrange
        OwnSignal signal = new OwnSignal();
        signal.setTakeProfit(BigDecimal.valueOf(50000.0));
        signal.setStopLoss(BigDecimal.valueOf(50000.0));
        signal.setCashType(CashType.DOLAR);
        signal.setSymbol("BTCUSDT");
        when(binanceSupport.getMarketPriceBigDecimal(syncRequestClient, "BTCUSDT")).thenReturn(BigDecimal.valueOf(50000));

        ownSignalService.checkOwnSignal(signal, syncRequestClient);

        verify(binanceSupport, times(1)).getMarketPriceBigDecimal(syncRequestClient, "BTCUSDT");
    }

    @Test
    void checkOwnSignal_WithNotNullStrategySetting_ShouldNotCheckTpAndSl(){
        OwnSignal signal = new OwnSignal();
        signal.setStrategySetting(new Strategy());

        ownSignalService.checkOwnSignal(signal, syncRequestClient);

        verify(binanceSupport, times(0)).getMarketPriceBigDecimal(syncRequestClient, "BTCUSDT");
    }
}