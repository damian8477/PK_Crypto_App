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



    @Test
    public void testCheckTpAndSl_WithPercentTpWithinRange() {
        OwnSignalServiceImpl ownSignalService = new OwnSignalServiceImpl(binanceSupport);
        OwnSignal signal = new OwnSignal();
        signal.setTpPercent(true);
        signal.setTakeProfit(BigDecimal.valueOf(30.0));
        signal.setSlPercent(true);
        signal.setStopLoss(BigDecimal.valueOf(40.0));

        assertDoesNotThrow(() -> ownSignalService.checkTpAndSl(signal, BigDecimal.valueOf(100.0)));
    }

    @Test
    public void testCheckTpAndSl_WithPercentTpOutOfRange() {
        OwnSignalServiceImpl ownSignalService = new OwnSignalServiceImpl(binanceSupport);
        OwnSignal signal = new OwnSignal();
        signal.setTpPercent(true);
        signal.setTakeProfit(BigDecimal.valueOf(110.0));

        assertThrows(IllegalArgumentException.class, () -> ownSignalService.checkTpAndSl(signal, BigDecimal.valueOf(100.0)));
    }

    @Test
    public void testCheckTpAndSl_WithPercentSlOutOfRange() {
        OwnSignalServiceImpl ownSignalService = new OwnSignalServiceImpl(binanceSupport);
        OwnSignal signal = new OwnSignal();
        signal.setSlPercent(true);
        signal.setTakeProfit(BigDecimal.valueOf(3));
        signal.setStopLoss(BigDecimal.valueOf(-10.0));

        assertThrows(IllegalArgumentException.class, () -> ownSignalService.checkTpAndSl(signal, BigDecimal.valueOf(100.0)));
    }

    @Test
    public void testCheckTpAndSl_WithNonPercentTpWithinRange() {
        OwnSignalServiceImpl ownSignalService = new OwnSignalServiceImpl(binanceSupport);
        OwnSignal signal = new OwnSignal();
        signal.setTpPercent(false);
        signal.setTakeProfit(BigDecimal.valueOf(55.0));
        signal.setSlPercent(false);
        signal.setStopLoss(BigDecimal.valueOf(47.0));

        assertDoesNotThrow(() -> ownSignalService.checkTpAndSl(signal, BigDecimal.valueOf(50.0)));
    }

    @Test
    public void testCheckTpAndSl_WithNonPercentTpOutOfRange() {
        OwnSignalServiceImpl ownSignalService = new OwnSignalServiceImpl(binanceSupport);
        OwnSignal signal = new OwnSignal();
        signal.setTpPercent(false);
        signal.setStopLoss(BigDecimal.valueOf(80.0));
        signal.setTakeProfit(BigDecimal.valueOf(700.0));
        assertThrows(IllegalArgumentException.class, () -> ownSignalService.checkTpAndSl(signal, BigDecimal.valueOf(100.0)));
    }

    @Test
    public void testCheckTpAndSl_WithNonPercentSlOutOfRange() {
        OwnSignalServiceImpl ownSignalService = new OwnSignalServiceImpl(binanceSupport);
        OwnSignal signal = new OwnSignal();
        signal.setTakeProfit(BigDecimal.valueOf(40000));
        signal.setSlPercent(false);
        signal.setStopLoss(BigDecimal.valueOf(-60.0));

        assertThrows(IllegalArgumentException.class, () -> ownSignalService.checkTpAndSl(signal, BigDecimal.valueOf(100.0)));
    }

}