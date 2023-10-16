package pl.coderslab.service.entity;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.User;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public User getUserWithUserSettings(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            Hibernate.initialize(user.getUserSetting());
            Hibernate.initialize(user.getStrategies());
            Hibernate.initialize(user.getOrders());
            Hibernate.initialize(user.getAlerts());
        }
        return user;
    }

    @Override
    @Transactional
    public User getUserWithUserSettingsByUserName(String userName) {
        User user = userRepository.findByUsername(userName);
        fillUser(user);
        return user;
    }

    @Override
    @Transactional
    public List<User> getUserList() {
        List<User> users = userRepository.findAll();
        users.forEach(this::fillUser);
        return users;
    }
    @Override
    @Transactional
    public List<User> getActiveUsers() {
        List<User> users = userRepository.findAllByActive(true);
        users.forEach(this::fillUser);
        return users;
    }

    @Override
    public User getUserBasic(String username) {
        return userRepository.findByUsername(username);
    }
    @Override
    public void fillUser(User user) {
        if (user != null) {
            Hibernate.initialize(user.getUserSetting());
            Hibernate.initialize(user.getStrategies());
            Hibernate.initialize(user.getOrders());
            Hibernate.initialize(user.getAlerts());
        }
    }


}
