package pl.coderslab.service.binance.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.market.ExchangeInfoEntry;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.interfaces.*;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.service.binance.SymbolExchangeService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenServiceImplTest {
    @Mock
    private SourceService sourceService;

    @Mock
    private BinanceBasicService binanceBasicService;

    @Mock
    private BinanceService binanceService;

    @Mock
    private SyncService syncService;

    @Mock
    private OrderService orderService;

    @Mock
    private TelegramBotService telegramBotService;

    @Mock
    private MessageService messageService;
    @Mock
    private SymbolExchangeService exchangeService;

    @InjectMocks
    private OpenServiceImpl openService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void newSignal_shouldOpenOrderWhenPositionRiskIsNullAndNoExistingOrders() {
        // Arrange
        CommonSignal signal = createTestSignal();
        Source source = createTestSource();
        User user = createTestUser();
        ExchangeInfoEntry exchangeInfoEntry = createTestExchangeInfoEntry();
        when(sourceService.findByName(signal.getSourceName())).thenReturn(source);
        when(binanceBasicService.getMarketPriceDouble(any(), any())).thenReturn(100.0);
        SymbolExchangeService.exchangeInfoEntries.add(exchangeInfoEntry);
        //when(SymbolExchangeService.exchangeInfoEntries.stream().filter(any()).findFirst()).thenReturn(java.util.Optional.of(exchangeInfoEntry));
        when(syncService.sync(user.getUserSetting().get(0))).thenReturn(mock(SyncRequestClient.class));
        when(mock(SyncRequestClient.class).getPositionRisk()).thenReturn(new ArrayList<>());

        // Act
        openService.newSignal(signal);

        // Assert
        verify(openService, times(1)).openSignal(eq(signal), any(), eq(exchangeInfoEntry), eq(user), eq(100.0), eq(source), any());
    }

    // Dodaj kolejne testy w miarę potrzeb, uwzględniając różne scenariusze

    private CommonSignal createTestSignal() {
        // Zwróć przykładowy sygnał dla testów
        return CommonSignal.builder()
                .symbol("BTCUSDT")
                .lever(10)
                .lot("0.003")
                .entryPrice(List.of(new BigDecimal(40000)))
                .positionSide(PositionSide.LONG)
                .isStrategy(true)
                .sourceName("RSI")
                .orderType(OrderType.MARKET)
                .takeProfit(List.of(new BigDecimal(45000)))
                .build();
    }

    private Source createTestSource() {
        // Zwróć przykładowe źródło dla testów
        Source source = new Source();
        Strategy strategy = new Strategy();
        strategy.setUsers(List.of(createTestUser()));
        source.setStrategies(List.of(strategy));

        return source;
    }

    private User createTestUser() {
        // Zwróć przykładowego użytkownika dla testów
        return User.builder()
                .username("user")
                .userSetting(List.of(new UserSetting()))
                .build();
    }

    private ExchangeInfoEntry createTestExchangeInfoEntry() {
        // Zwróć przykładową informację o wymianie dla testów
        return ExchangeInfoEntry.builder()
                .symbol("BTCUSDT")
                .build();
    }
}