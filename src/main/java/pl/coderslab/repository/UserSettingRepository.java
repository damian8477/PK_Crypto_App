package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.user.UserSetting;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

}
