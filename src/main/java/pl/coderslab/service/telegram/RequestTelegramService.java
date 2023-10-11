package pl.coderslab.service.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.repository.UserRepository;
import pl.coderslab.repository.UserSettingRepository;
import pl.coderslab.service.entity.UserService;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestTelegramService {
    private final UserSettingRepository userSettingRepository;
    private final UserService userService;
    private final TelegramCodeService telegramCodeService;
    private final UserRepository userRepository;


    public String newMessege(String chatId, String mess) throws IOException {
        System.out.println(chatId);
        if(userSettingRepository.existsByTelegramChatId(chatId)){
            UserSetting userSetting = userSettingRepository.findByTelegramChatId(chatId);

            // todo tutaj moze jakies menu z czyms tam zrobic , może z otwieraniem zlecen z palca, z tp i sl itd
            return "Oczekuj na powiadomienia";
        } else {
            String[] split = mess.split(" ");
            if(split.length == 2){
                String username = split[0];
                String code = split[1];
                if(userRepository.existsByUsername(username)){
                    User user = userService.getUserWithUserSettingsByUserName(username);
                    return telegramCodeService.checkCode(user, code, chatId);
                }
                return "Podane dane są błędne, spróbuj ponownie";
            }
            return "Użytkownik nie autoryzowany, podaj login i klucz\nnp. \"arek121 333333\"";
        }
    }
}
