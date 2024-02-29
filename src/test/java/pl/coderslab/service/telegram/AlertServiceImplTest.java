package pl.coderslab.service.telegram;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.coderslab.entity.alert.Alert;
import pl.coderslab.interfaces.SyncService;
import pl.coderslab.interfaces.TelegramBotService;
import pl.coderslab.interfaces.UserSettingService;
import pl.coderslab.repository.AlertRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AlertServiceImplTest {


    @Mock
    private AlertRepository alertRepositoryMock;
    @Mock
    private SyncService syncServiceMock;
    @Mock
    private TelegramBotService telegramBotServiceMock;
    @Mock
    private UserSettingService userSettingServiceMock;

    @InjectMocks
    private AlertServiceImpl alertService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testGetAlertList_GroupBySymbolName() {
        List<Alert> alerts = new ArrayList<>();
        alerts.add(Alert.builder().id(1L).symbolName("BTCUSDT").price(BigDecimal.valueOf(50000.0)).build());
        alerts.add(Alert.builder().id(2L).symbolName("BTCUSDT").price(BigDecimal.valueOf(51000.0)).build());
        alerts.add(Alert.builder().id(3L).symbolName("ETHUSDT").price(BigDecimal.valueOf(2000.0)).build());
        when(alertRepositoryMock.findAll()).thenReturn(alerts);

        List<Map<String, List<Alert>>> result = alertService.getAlertList(alerts);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).get("ETHUSDT").size());
        assertEquals(2, result.get(1).get("BTCUSDT").size());
    }

}