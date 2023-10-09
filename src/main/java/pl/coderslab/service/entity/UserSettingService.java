package pl.coderslab.service.entity;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.repository.UserSettingRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingService {
    private final UserSettingRepository userSettingRepository;

    @Transactional
    public UserSetting getUserSetting(int id) {
        UserSetting userSetting = userSettingRepository.findById(id);
        //Hibernate.initialize(userSetting.getUser());
        return userSetting;
    }
}
