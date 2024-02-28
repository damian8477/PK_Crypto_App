package pl.coderslab.service.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.interfaces.BinanceBasicService;
import pl.coderslab.interfaces.BinanceService;
import pl.coderslab.model.AlertSetting;
import pl.coderslab.model.CryptoName;
import pl.coderslab.repository.SymbolRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SymbolServiceImplTest {
    @Mock
    private BinanceService binanceServiceMock;
    @Mock
    private BinanceBasicService binanceBasicServiceMock;
    @Mock
    private SymbolRepository symbolRepositoryMock;

    @InjectMocks
    private SymbolServiceImpl symbolService;


    @Test
    public void testFindById_ExistingSymbol(){
        Integer symbolId = 1;
        Symbol symbol = new Symbol();
        symbol.setId(symbolId);
        when(symbolRepositoryMock.findById(symbolId)).thenReturn(Optional.of(symbol));

        Symbol foundSymbol = symbolService.findById(symbolId);

        assertNotNull(foundSymbol);
        assertEquals(symbol, foundSymbol);
        verify(symbolRepositoryMock).findById(symbolId);
    }
    @Test
    public void testFindById_NonExistingSymbol() {
        Integer symbolId = 2;
        when(symbolRepositoryMock.findById(symbolId)).thenReturn(Optional.empty());

        Symbol foundSymbol = symbolService.findById(symbolId);

        assertNull(foundSymbol);
        verify(symbolRepositoryMock).findById(symbolId);
    }

    @Test
    public void testFindById_NullInput(){

        Symbol foundSymbol = symbolService.findById(null);

        assertNull(foundSymbol);
        verify(symbolRepositoryMock).findById(any());
    }

    @Test
    public void checkSymbol_symbolAlreadyExist(){
        Symbol symbol = new Symbol();
        symbol.setName("BTCUSDT");
        CryptoName cryptoName = new CryptoName();
        cryptoName.setSymbol("BTCUSDT");
        when(binanceServiceMock.getSymbols()).thenReturn(List.of(cryptoName));

        boolean result = symbolService.checkSymbol(symbol);

        assertFalse(result);
    }

    @Test
    public void checkSymbol_symbolNotFoundInvalidSymbol(){
        Symbol symbol = new Symbol();
        symbol.setName("ETHUSDT");
        CryptoName cryptoName = new CryptoName();
        cryptoName.setSymbol("BTCUSDT");
        when(binanceServiceMock.getSymbols()).thenReturn(List.of(cryptoName));
        when(binanceServiceMock.getSymbolNames()).thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> symbolService.checkSymbol(symbol));
        verify(binanceServiceMock).getSymbolNames();
    }

    @Test
    void checkSymbol_validSymbol(){
        Symbol symbol = new Symbol();
        symbol.setName("ethusdt");
        CryptoName cryptoName = new CryptoName();
        cryptoName.setSymbol("BTCUSDT");
        when(binanceServiceMock.getSymbols()).thenReturn(List.of(cryptoName));
        when(binanceServiceMock.getSymbolNames()).thenReturn(List.of("BTCUSDT", "ETHUSDT"));

        boolean result = symbolService.checkSymbol(symbol);

        assertTrue(result);
        assertEquals("ETHUSDT", symbol.getName());
        verify(binanceServiceMock).getSymbolNames();
    }

    @Test
    void getBasicAlert_validResult(){
        String symbolName = "BTCUSDT";
        BigDecimal marketPrice = new BigDecimal("50000.00");
        when(binanceBasicServiceMock.getMarketPriceBigDecimal(null, symbolName)).thenReturn(marketPrice);

        AlertSetting result = symbolService.getBasicAlert(symbolName);

        assertNotNull(result);
        assertEquals(result.getSymbol(), symbolName);
        assertEquals(result.getMarketPrice(), marketPrice);
    }
}