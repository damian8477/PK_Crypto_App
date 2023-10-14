package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;

import java.util.List;

public interface UserSettingRepository extends JpaRepository<UserSetting, Integer> {
    UserSetting findById(int id);

    boolean existsByTelegramChatId(String chatId);

    UserSetting findByTelegramChatId(String telegramChatId);

    List<UserSetting> findAllByUserId(long userId);


}
