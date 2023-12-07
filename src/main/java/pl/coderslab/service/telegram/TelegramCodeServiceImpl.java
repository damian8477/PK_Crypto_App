package pl.coderslab.service.telegram;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.TelegramCode;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.interfaces.MessageService;
import pl.coderslab.interfaces.TelegramCodeService;
import pl.coderslab.repository.TelegramCodeRepository;
import pl.coderslab.repository.UserSettingRepository;

import java.util.Random;

/**
 * Implementation of the {@link TelegramCodeService} interface providing methods
 * for handling Telegram codes and user authentication.
 */
@Service
@RequiredArgsConstructor
public class TelegramCodeServiceImpl implements TelegramCodeService {
    private final UserSettingRepository userSettingRepository;
    private final TelegramCodeRepository telegramCodeRepository;
    private final MessageService messageService;


    private static final Logger logger = LoggerFactory.getLogger(TelegramCodeServiceImpl.class);

    /**
     * Checks if the provided Telegram code matches the expected code for the user.
     * If the codes match, the user's Telegram chat ID is updated, and the Telegram code is deleted.
     * If the number of attempts exceeds 3, the Telegram code is deleted.
     *
     * @param user   the user for whom the code is checked.
     * @param code   the Telegram code provided by the user.
     * @param chatId the Telegram chat ID.
     * @return a message indicating the result of the code check.
     */
    @Override
    public String checkCode(User user, String code, String chatId) {
        if (user.getUserSetting() != null && telegramCodeRepository.existsByUser(user)) {
            UserSetting userSetting = user.getUserSetting().get(0);
            TelegramCode telegramCode = telegramCodeRepository.getByUser(user);
            if (code.equals(telegramCode.getNumberCode())) {
                userSetting.setTelegramChatId(chatId);
                userSettingRepository.save(userSetting);
                telegramCodeRepository.deleteById(telegramCode.getId());
                return String.format(messageService.getTelegramUserAuthConfirm(null), user.getFirstName());
            } else {
                telegramCode.setTryCount(telegramCode.getTryCount() + 1);
                if (telegramCode.getTryCount() > 3) {
                    telegramCodeRepository.deleteById(telegramCode.getId());
                } else {
                    telegramCodeRepository.save(telegramCode);
                }
            }
        }
        return messageService.getTelegramUserAuthError(null);
    }

    /**
     * Generates and retrieves the Telegram token code for the user.
     * If the user already has a Telegram code, the existing code is returned.
     * If not, a new Telegram code is generated, saved, and returned.
     *
     * @param length the length of the generated code.
     * @param user   the user for whom the code is generated.
     * @return the generated Telegram code.
     */
    @Override
    public String getCode(int length, User user) {
        if (telegramCodeRepository.existsByUser(user)) {
            return telegramCodeRepository.getByUser(user).getNumberCode();
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

    /**
     * Generates a random code of the specified length.
     *
     * @param length the length of the generated code.
     * @return the generated random code.
     */
    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
        }
        return code.toString();
    }
}
