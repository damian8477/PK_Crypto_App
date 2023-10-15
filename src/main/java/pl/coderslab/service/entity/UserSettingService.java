package pl.coderslab.service.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.repository.UserSettingRepository;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.Objects.isNull;

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

    public boolean userSettingExist(List<UserSetting> userSetting){
        if(!isNull(userSetting)){
            if(userSetting.size() > 0){
                return true;
            }
        }
        return false;
    }
}
