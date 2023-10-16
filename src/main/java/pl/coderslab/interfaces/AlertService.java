package pl.coderslab.interfaces;

import pl.coderslab.entity.alert.Alert;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.Direction;
import pl.coderslab.model.AlertSetting;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AlertService {
    List<Map<String, List<Alert>>> getAlertList(List<Alert> alerts);

    void addAlert(User user, AlertSetting alert);

    Direction getDirection(BigDecimal price, BigDecimal marketPrice);

    void checkAlerts();

    void checkAlert(BigDecimal price, Alert alert);
}
