package pl.coderslab.service.entity;

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
            Hibernate.initialize(user.getUserSetting());
            Hibernate.initialize(user.getStrategies());
            Hibernate.initialize(user.getOrders());
        }
        return user;
    }

    @Transactional
    public User getUserWithUserSettingsByUserName(String userName) {
        User user = userRepository.findByUsername(userName);
        fillUser(user);
        return user;
    }

    @Transactional
    public long getUserId(String userName) {
        long userId = 0;
        try{
            userId = userRepository.getUserId(userName);
        }catch (Exception e){
            System.out.println(e);
        }
        return userId;
    }

    public void fillUser(User user){
        if (user != null) {
            Hibernate.initialize(user.getUserSetting());
            Hibernate.initialize(user.getStrategies());
            Hibernate.initialize(user.getOrders());
        }
    }



}
