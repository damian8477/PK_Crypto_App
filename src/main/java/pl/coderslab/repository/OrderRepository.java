package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.orders.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
   List<Order> findByUserId(Long userId);
    void deleteAllByUserId(long userId);
}
