package pl.coderslab.service.scheduled;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.TelegramCode;
import pl.coderslab.entity.user.User;
import pl.coderslab.repository.TelegramCodeRepository;
import pl.coderslab.repository.UserTokenRepository;
import pl.coderslab.service.entity.UserService;
import pl.coderslab.service.telegram.AlertService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledService {
    private final TelegramCodeRepository telegramCodeRepository;
    private final AlertService alertService;
    private final CheckUserOrderService checkUserOrderService;
    private final UserService userService;
    private final UserTokenRepository userTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledService.class);
    private static int count = 0;

    //todo  czasy kiedy co ma być sprawdzane można trzymać w bazie
    //todo pobranie tych czasów np. raz na godzine lub wymuszenie z telegrama
    //todo dla zlecen innych niż MARKET sprawdzanie czy sie otworzylo i dopisywanie zlecen TP i SL

    @Scheduled(fixedDelay = 60000, initialDelay = 1000)
    public void check() {
        List<User> users = userService.getActiveUsers();
        logger.info("Scheduled counter: " + count);
        if(count % 1 == 0) {
            checkUserOrderService.checkInActiveOrder(users);
        }
        if (count % 2 == 0) {
            checkTokensExpirationTime();
        }
        if (count % 4 == 0) {
//            checkBotOrder();//todo co 3 minuty, trzeba sprawdzać świeczki 3 lub 5 minutowe
//            checkStrategy();
        }
        if (count % 5 == 0 || count == 0)
            alertService.checkAlerts();
        count++;
        if (count > 360) {
            count = 0;
        }
    }

    private void checkTokensExpirationTime() {
        List<TelegramCode> telegramCodes = telegramCodeRepository.findAllByExpiredCode(LocalDateTime.now().minusMinutes(15L));
        telegramCodeRepository.deleteAll(telegramCodes);
        userTokenRepository.deleteAllByExpiredCode(LocalDateTime.now().minusMinutes(15L));

    }
}
