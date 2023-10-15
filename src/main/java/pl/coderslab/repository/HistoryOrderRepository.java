package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.orders.HistoryOrder;

import java.util.List;

public interface HistoryOrderRepository extends JpaRepository<HistoryOrder, Long> {
    List<HistoryOrder> findAllByUserId(Long userId);
}
