package pl.coderslab.interfaces;

import pl.coderslab.entity.user.User;

import javax.transaction.Transactional;
import java.util.List;

public interface UserService {
    @Transactional
    User getUserWithUserSettings(Long userId);

    @Transactional
    User getUserWithUserSettingsByUserName(String userName);

    @Transactional
    List<User> getUserList();

    @Transactional
    List<User> getActiveUsers();

    User getUserBasic(String username);

    void fillUser(User user);
}
