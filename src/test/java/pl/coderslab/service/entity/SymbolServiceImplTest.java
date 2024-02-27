package pl.coderslab.service.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.interfaces.BinanceBasicService;
import pl.coderslab.interfaces.BinanceService;
import pl.coderslab.repository.SymbolRepository;

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

}