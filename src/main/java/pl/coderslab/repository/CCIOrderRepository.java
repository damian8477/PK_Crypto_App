package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.strategy.CCIOrder;

import java.time.LocalDateTime;
import java.util.List;

public interface CCIOrderRepository extends JpaRepository<CCIOrder, Long> {
    List<CCIOrder> findAllByActive(boolean active);
    List<CCIOrder> findAllByOpenTime(LocalDateTime localDateTime);
}
