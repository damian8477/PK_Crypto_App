package pl.coderslab.service.scheduled;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.coderslab.controller.user.RegistrationController;
import pl.coderslab.entity.user.TelegramCode;
import pl.coderslab.model.AlertSetting;
import pl.coderslab.repository.TelegramCodeRepository;
import pl.coderslab.service.telegram.AlertService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledService {
    private final TelegramCodeRepository telegramCodeRepository;
    private final AlertService alertService;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledService.class);
    private static int count = 0;

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void check() {
        System.out.println(count);
        if (count % 6 == 0) {
            checkTelegramExpirationTime();
        }
        if (count % 12 == 0 || count == 0)
            alertService.checkAlerts();
        count++;
        if (count > 360) {
            count = 0;
        }
    }

    private void checkTelegramExpirationTime() {
        List<TelegramCode> telegramCodes = telegramCodeRepository.findAllByExpiredCode(LocalDateTime.now().minusMinutes(15L));
        telegramCodeRepository.deleteAll(telegramCodes);
    }
}
