package pl.coderslab.interfaces;

import pl.coderslab.entity.user.User;

public interface TelegramCodeService {
    String checkCode(User user, String code, String chatId);

    String getCode(int length, User user);
}
