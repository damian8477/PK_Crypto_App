package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.user.UserSetting;

import java.util.List;

public interface UserSettingRepository extends JpaRepository<UserSetting, Integer> {
    public UserSetting findById(int id);


}
