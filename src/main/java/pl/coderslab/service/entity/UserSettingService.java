package pl.coderslab.service.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.repository.UserSettingRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSettingService {
    private final UserSettingRepository userSettingRepository;

    @Transactional
    public UserSetting getUserSetting(int id) {
        return userSettingRepository.findById(id);
    }

    @Transactional
    public List<UserSetting> getUserSettingByUserId(Long userId) {
        return userSettingRepository.findAllByUserId(userId);
    }
}
