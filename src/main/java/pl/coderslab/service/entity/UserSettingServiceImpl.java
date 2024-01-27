package pl.coderslab.service.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.interfaces.UserSettingService;
import pl.coderslab.repository.UserSettingRepository;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class UserSettingServiceImpl implements UserSettingService {
    private final UserSettingRepository userSettingRepository;

    @Override
    @Transactional
    public UserSetting getUserSetting(int id) {
        return userSettingRepository.findById(id);
    }

    @Override
    @Transactional
    public List<UserSetting> getUserSettingByUserId(Long userId) {
        return userSettingRepository.findAllByUserId(userId);
    }

    @Override
    public boolean userSettingExist(List<UserSetting> userSetting) {
        return !isNull(userSetting) && (!userSetting.isEmpty());
    }
}
