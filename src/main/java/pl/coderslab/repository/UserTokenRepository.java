package pl.coderslab.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.coderslab.entity.user.UserToken;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    UserToken findByUserId(Long userId);

    void deleteAllByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("delete from UserToken t where t.created < ?1")
    void deleteAllByExpiredCode(LocalDateTime time);
}
