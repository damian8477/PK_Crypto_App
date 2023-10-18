package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.alert.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    void deleteAllByUserId(long userId);
}
