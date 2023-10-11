package pl.coderslab.service.scheduled;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.TelegramCode;
import pl.coderslab.repository.TelegramCodeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledService {
    private final TelegramCodeRepository telegramCodeRepository;

    private static int count = 0;
    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void check(){
        System.out.println(count);
        if(count % 6 == 0){
            checkTelegramExpirationTime();
        }
        count++;
        if(count > 360) {
            count = 0;
        }
    }

    private void checkTelegramExpirationTime(){
        List<TelegramCode> telegramCodes = telegramCodeRepository.findAllByExpiredCode(LocalDateTime.now().minusMinutes(15L));
        telegramCodeRepository.deleteAll(telegramCodes);
    }
}
