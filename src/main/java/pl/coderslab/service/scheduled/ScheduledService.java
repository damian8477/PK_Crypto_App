package pl.coderslab.service.scheduled;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.strategy.CCIOrder;
import pl.coderslab.entity.user.TelegramCode;
import pl.coderslab.entity.user.User;
import pl.coderslab.interfaces.AlertService;
import pl.coderslab.interfaces.CheckUserOrderService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.repository.TelegramCodeRepository;
import pl.coderslab.repository.UserTokenRepository;
import pl.coderslab.strategy.indicators.cci.CCIStrategy;
import pl.coderslab.strategy.service.Strategy110Service;
import pl.coderslab.strategy.service.Strategy111Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledService {
    private final TelegramCodeRepository telegramCodeRepository;
    private final AlertService alertService;
    private final CheckUserOrderService checkUserOrderService;
    private final UserService userService;
    private final UserTokenRepository userTokenRepository;
    private final Strategy110Service strategy110Service;
    private final Strategy111Service strategy111Service;
    private final CCIStrategy cciStrategy;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledService.class);
    private int count = 1;

    //todo  czasy kiedy co ma być sprawdzane można trzymać w bazie
    //todo pobranie tych czasów np. raz na godzine lub wymuszenie z telegrama
    //todo dla zlecen innych niż MARKET sprawdzanie czy sie otworzylo i dopisywanie zlecen TP i SL


    @Scheduled(fixedDelay = 60000, initialDelay = 1000)
    public void check() {
        List<User> users = userService.getActiveUsers();
        if(count % 60 == 0){
            logger.info("Scheduled counter: " + count + " " + LocalDateTime.now());
        }
        if (count == 1) {
            strategy110Service.downloadCryptoNameList();
            strategy110Service.checkCoinInStrategy();
            cciStrategy.searchCCI();
            strategy111Service.searchNewOrder(null);
        }
        if (count % 1 == 0) {
            checkUserOrderService.checkInActiveOrder(users);
            strategy110Service.checkOrderStatusBot(null);
            strategy111Service.checkOrderStatusBot(null);
        }
        if (count % 2 == 0) {
            checkTokensExpirationTime();
        }
        if (count % 4 == 0) {
            strategy110Service.searchNewOrder(null);
        }
        if (count % 5 == 0){
            alertService.checkAlerts();
        }
        if (count % 15 == 0){
            cciStrategy.searchCCI();
            strategy111Service.searchNewOrder(null);
        }
        count++;
        if (count > 360) {
            count = 1;
        }
    }

    private void checkTokensExpirationTime() {
        List<TelegramCode> telegramCodes = telegramCodeRepository.findAllByExpiredCode(LocalDateTime.now().minusMinutes(15L));
        telegramCodeRepository.deleteAll(telegramCodes);
        userTokenRepository.deleteAllByExpiredCode(LocalDateTime.now().minusMinutes(15L));
    }
}
