package pl.coderslab.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.User;
import pl.coderslab.repository.UserRepository;

import javax.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User getUserWithUserSettings(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            // Inicjuj kolekcję userSetting
            Hibernate.initialize(user.getUserSetting());
            Hibernate.initialize(user.getStrategies());
            Hibernate.initialize(user.getOrders());
        }
        return user;
    }
}
