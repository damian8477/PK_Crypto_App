package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.orders.HistoryOrder;

public interface HistoryOrderRepository extends JpaRepository<HistoryOrder, Long> {
}
