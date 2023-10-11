package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.coderslab.entity.user.TelegramCode;
import pl.coderslab.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TelegramCodeRepository extends JpaRepository<TelegramCode, Long> {
    TelegramCode getByUser(User user);
    boolean existsByUser(User user);
    void deleteByUser(User user);
    @Query("select t from TelegramCode t where t.created < ?1")
    List<TelegramCode> findAllByExpiredCode(LocalDateTime time);

}
