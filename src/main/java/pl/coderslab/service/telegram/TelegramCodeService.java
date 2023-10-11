package pl.coderslab.service.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.TelegramCode;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.repository.TelegramCodeRepository;
import pl.coderslab.repository.UserRepository;
import pl.coderslab.repository.UserSettingRepository;
import pl.coderslab.service.entity.UserService;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class TelegramCodeService {
    private final UserSettingRepository userSettingRepository;
    private final UserRepository userRepository;
    private final TelegramCodeRepository telegramCodeRepository;


    public String checkCode(User user, String code, String chatId) {
        if (user.getUserSetting() != null && telegramCodeRepository.existsByUser(user)) {
            UserSetting userSetting = user.getUserSetting().get(0);
            TelegramCode telegramCode = telegramCodeRepository.getByUser(user);
            if (code.equals(telegramCode.getNumberCode())) {
                userSetting.setTelegramChatId(chatId);
                userSettingRepository.save(userSetting);
                telegramCodeRepository.deleteById(telegramCode.getId());
                return String.format("Witaj %s, od dziś będziesz otrzymywać alerty oraz powiadomienia.", user.getFirstName());
            } else {
                telegramCode.setTryCount(telegramCode.getTryCount() + 1);
                if (telegramCode.getTryCount() > 3) {
                    telegramCodeRepository.deleteById(telegramCode.getId());
                } else {
                    telegramCodeRepository.save(telegramCode);
                }
            }
        }
        return "Użytkownik o podanym loginie nie istnieje lub nie oczekuje na autoryzacje telegrama\nPo 3 próbach, klucz zostanie usunięty";
    }


    public String getCode(int length, User user) {
        if (telegramCodeRepository.existsByUser(user)) {
            return telegramCodeRepository.getByUser(user).getNumberCode();
            //telegramCodeRepository.deleteByUser(user);
        } else {
            TelegramCode telegramCode = TelegramCode.builder()
                    .numberCode(generateRandomCode(length))
                    .tryCount(0)
                    .user(user)
                    .build();
            telegramCodeRepository.save(telegramCode);
            return telegramCode.getNumberCode();
        }

    }

    public String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // Losujemy cyfrę od 0 do 9
            code.append(digit);
        }

        return code.toString();
    }


}
