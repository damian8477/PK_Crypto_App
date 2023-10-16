package pl.coderslab.interfaces;

import pl.coderslab.entity.user.UserSetting;

import javax.transaction.Transactional;
import java.util.List;

public interface UserSettingService {
    @Transactional
    UserSetting getUserSetting(int id);

    @Transactional
    List<UserSetting> getUserSettingByUserId(Long userId);

    boolean userSettingExist(List<UserSetting> userSetting);
}
