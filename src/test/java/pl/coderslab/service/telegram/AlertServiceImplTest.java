package pl.coderslab.service.telegram;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.coderslab.TestFixtures;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.entity.alert.Alert;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.enums.Direction;
import pl.coderslab.interfaces.SyncService;
import pl.coderslab.interfaces.TelegramBotService;
import pl.coderslab.interfaces.UserSettingService;
import pl.coderslab.model.AlertSetting;
import pl.coderslab.repository.AlertRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    public void getAlertList_GroupBySymbolName() {
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

    @Test
    public void getAlertList_groupBySymbolName() {
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

    @Test
    public void getAlertList_emptyAlertList() {
        List<Alert> alerts = new ArrayList<>();
        when(alertRepositoryMock.findAll()).thenReturn(alerts);

        List<Map<String, List<Alert>>> result = alertService.getAlertList(alerts);

        assertEquals(0, result.size());
    }

    @Test
    public void addAlert_CallsSaveAlertWhenAlertPriceAndPositionSideNotNull() {
        User user = new User();
        AlertSetting alertSetting = AlertSetting.builder()
                .alertPrice1(BigDecimal.valueOf(50000.0))
                .alertPrice2(BigDecimal.valueOf(51000.0))
                .alertPrice3(BigDecimal.valueOf(52000.0))
                .alertPrice4(BigDecimal.valueOf(53000.0))
                .alertPrice5(BigDecimal.valueOf(54000.0))
                .marketPrice(BigDecimal.valueOf(49000.0))
                .positionSide1(PositionSide.LONG)
                .positionSide2(PositionSide.LONG)
                .positionSide3(PositionSide.LONG)
                .positionSide4(PositionSide.LONG)
                .positionSide5(PositionSide.LONG)
                .build();

        alertService.addAlert(user, alertSetting);

        verify(alertRepositoryMock, times(5)).save(any(Alert.class));
    }

    @Test
    public void addAlert_DoesNotCallSaveAlertWhenAlertPriceIsNull() {
        User user = new User();
        AlertSetting alertSetting = AlertSetting.builder()
                .build();

        alertService.addAlert(user, alertSetting);

        verify(alertRepositoryMock, never()).save(any(Alert.class));
    }

    @Test
    public void getDirection_priceLessThanMarketPrice() {
        BigDecimal price = new BigDecimal("1000.0");
        BigDecimal marketPrice = new BigDecimal("2000.0");

        Direction result = alertService.getDirection(price, marketPrice);

        assertEquals(result, Direction.DOWN);
    }

    @Test
    public void getDirection_priceGreaterThanMarketPrice() {
        BigDecimal price = new BigDecimal("2000.0");
        BigDecimal marketPrice = new BigDecimal("1000.0");

        Direction result = alertService.getDirection(price, marketPrice);

        assertEquals(result, Direction.UP);
    }

    @Test
    public void checkAlert_UpDirectionPriceAboveAlertPrice_SendsTelegramMessageAndDeletesAlert() {
        Alert alert = new Alert();
        alert.setId(1L);
        alert.setSymbolName("BTCUSDT");
        alert.setPrice(BigDecimal.valueOf(50000.0));
        alert.setDirection(Direction.UP);
        User user = TestFixtures.user();
        alert.setUser(user);
        UserSetting userSetting = TestFixtures.userSetting(user);
        BigDecimal price = BigDecimal.valueOf(55000.0);
        when(userSettingServiceMock.getUserSettingByUserId(user.getId())).thenReturn(List.of(userSetting));

        alertService.checkAlert(price, alert);

        verify(telegramBotServiceMock).sendMessage(userSetting.getTelegramChatId(), "Alert! \uD83D\uDCC8 BTCUSDT $50000.0");
        verify(alertRepositoryMock).deleteById(anyLong());
    }


    @Test
    public void checkAlert_DownDirectionPriceBelowAlertPrice_SendsTelegramMessageAndDeletesAlert() {
        Alert alert = new Alert();
        alert.setId(1L);
        alert.setSymbolName("BTCUSDT");
        alert.setPrice(BigDecimal.valueOf(50000.0));
        alert.setDirection(Direction.DOWN);
        User user = TestFixtures.user();
        alert.setUser(user);
        UserSetting userSetting = TestFixtures.userSetting(user);
        BigDecimal price = BigDecimal.valueOf(45000.0);
        when(userSettingServiceMock.getUserSettingByUserId(user.getId())).thenReturn(List.of(userSetting));

        alertService.checkAlert(price, alert);

        verify(telegramBotServiceMock).sendMessage(userSetting.getTelegramChatId(), "Alert! \uD83D\uDCC9 BTCUSDT $50000.0");
        verify(alertRepositoryMock).deleteById(anyLong());

    }

    @Test
    public void checkAlert_NoEmoticon_NotifiesNoUserSettings() {
        Alert alert = new Alert();
        alert.setId(1L);
        alert.setSymbolName("BTCUSDT");
        alert.setPrice(BigDecimal.valueOf(50000.0));
        alert.setDirection(Direction.UP);
        User user = TestFixtures.user();
        alert.setUser(user);
        UserSetting userSetting = TestFixtures.userSetting(user);
        BigDecimal price = BigDecimal.valueOf(45000.0);
        when(userSettingServiceMock.getUserSettingByUserId(user.getId())).thenReturn(List.of(userSetting));

        alertService.checkAlert(price, alert);

        verify(telegramBotServiceMock, never()).sendMessage(anyString(), anyString());
        verify(alertRepositoryMock, never()).deleteById(anyLong());

    }

    //todo test for checkAlert()
}