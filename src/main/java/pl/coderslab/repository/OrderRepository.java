package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.orders.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}