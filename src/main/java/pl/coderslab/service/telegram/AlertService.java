package pl.coderslab.service.telegram;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.binance.client.model.trade.PositionRisk;
import pl.coderslab.controller.user.RegistrationController;
import pl.coderslab.entity.alert.Alert;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.enums.Direction;
import pl.coderslab.model.AlertSetting;
import pl.coderslab.repository.AlertRepository;
import pl.coderslab.repository.UserSettingRepository;
import pl.coderslab.service.binance.SyncService;
import pl.coderslab.service.entity.UserSettingService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final SyncService syncService;
    private final TelegramBotService telegramBotService;
    private final UserSettingService userSettingService;

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);


    public List<Map<String, List<Alert>>> getAlertList(List<Alert> alerts) {
        return alerts.stream()
                .collect(Collectors.groupingBy(Alert::getSymbolName))
                .entrySet()
                .stream()
                .map(entry -> {
                    Map<String, List<Alert>> map = new HashMap<>();
                    map.put(entry.getKey(), entry.getValue());
                    return map;
                })
                .toList();
    }


    public void addAlert(User user, AlertSetting alert) {
        for (int i = 1; i <= 5; i++) {
            BigDecimal alertPrice = getAlertPrice(alert, i);
            PositionSide positionSide = getPositionSide(alert, i);

            if (!isNull(alertPrice) && !isNull(positionSide)) {
                saveAlert(user, alert.getSymbol(), alertPrice, positionSide, getDirection(alertPrice, alert.getMarketPrice()));
            }
        }
    }

    public Direction getDirection(BigDecimal price, BigDecimal marketPrice) {
        if (marketPrice.compareTo(price) < 0) {
            return Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }

    public void checkAlerts() {
        List<Alert> alerts = alertRepository.findAll();
        SyncRequestClient sync = syncService.sync(null);
        List<String> symbols = alerts.stream()
                .map(Alert::getSymbolName)
                .distinct().toList();
        Map<String, BigDecimal> priceMap = sync.getPositionRisk().stream()
                .filter(s -> symbols.contains(s.getSymbol()))
                .filter(s -> s.getPositionSide().equals("LONG"))
                .collect(Collectors.toMap(PositionRisk::getSymbol, PositionRisk::getMarkPrice));
        alerts.forEach(alert -> {
            BigDecimal price = priceMap.get(alert.getSymbolName());
            checkAlert(price, alert);
        });
    }


    public void checkAlert(BigDecimal price, Alert alert) {
        String emoticon = null;
        switch (alert.getDirection()) {
            case UP -> {
                if (price.compareTo(alert.getPrice()) > 0) {
                    emoticon = "\uD83D\uDCC8";
                }
            }
            case DOWN -> {
                if (price.compareTo(alert.getPrice()) < 0) {
                    emoticon = "\uD83D\uDCC9";
                }
            }
        }
        if (!isNull(emoticon)) {
            String message = String.format("Alert! %s %s $%s", emoticon, alert.getSymbolName(), alert.getPrice());
            try {
                List<UserSetting> userSettingList = userSettingService.getUserSettingByUserId(alert.getUser().getId());
                if (!isNull(userSettingList)) {
                    telegramBotService.sendMessage(userSettingList.get(0).getTelegramChatId(), message);
                    alertRepository.deleteById(alert.getId());
                }
            } catch (TelegramApiException e) {
                logger.error(e.toString());
            }
        }
    }

    private BigDecimal getAlertPrice(AlertSetting alert, int index) {
        return switch (index) {
            case 1 -> alert.getAlertPrice1();
            case 2 -> alert.getAlertPrice2();
            case 3 -> alert.getAlertPrice3();
            case 4 -> alert.getAlertPrice4();
            case 5 -> alert.getAlertPrice5();
            default -> null;
        };
    }

    private PositionSide getPositionSide(AlertSetting alert, int index) {
        return switch (index) {
            case 1 -> alert.getPositionSide1();
            case 2 -> alert.getPositionSide2();
            case 3 -> alert.getPositionSide3();
            case 4 -> alert.getPositionSide4();
            case 5 -> alert.getPositionSide5();
            default -> null;
        };
    }

    private void saveAlert(User user, String symbol, BigDecimal price, PositionSide positionSide, Direction direction) {
        alertRepository.save(Alert.builder()
                .symbolName(symbol)
                .user(user)
                .positionSide(positionSide)
                .price(price)
                .direction(direction)
                .build());
    }


}
