package pl.coderslab.interfaces;

import pl.coderslab.entity.user.User;

import javax.transaction.Transactional;
import java.util.List;

public interface UserService {
    User getUserWithUserSettings(Long userId);

    void deleteById(Long userId);

    User getUserWithUserSettingsByUserName(String userName);

    List<User> getUserList();

    List<User> getActiveUsers();

    User getUserBasic(String username);

    void fillUser(User user);
}
