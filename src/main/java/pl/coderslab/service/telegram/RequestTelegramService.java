package pl.coderslab.service.telegram;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.interfaces.MessageService;
import pl.coderslab.interfaces.TelegramCodeService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.repository.UserRepository;
import pl.coderslab.repository.UserSettingRepository;

import static java.util.Objects.isNull;


@Service
@RequiredArgsConstructor
public class RequestTelegramService {
    private final UserSettingRepository userSettingRepository;
    private final UserService userService;
    private final TelegramCodeService telegramCodeService;
    private final UserRepository userRepository;
    private final MessageService messageService;

    public String newMessage(String chatId, String mess) {
        UserSetting userSetting = userSettingRepository.findByTelegramChatId(chatId);
        if (!isNull(userSetting)) {
            if (mess.equals("/logout")) {
                userSetting.setTelegramChatId("");
                userSettingRepository.save(userSetting);
                return messageService.getTelegramUserLogout(null);
            }
            return messageService.getTelegramUserWait(null);
        } else {
            String[] split = mess.split(" ");
            if (split.length == 2) {
                String username = split[0];
                String code = split[1];
                if (userRepository.existsByUsername(username)) {
                    User user = userService.getUserWithUserSettingsByUserName(username);
                    return telegramCodeService.checkCode(user, code, chatId);
                }
                return messageService.getTelegramUserError(null);
            }
            return messageService.getTelegramUserNotAuthorized(null);
        }
    }
}
