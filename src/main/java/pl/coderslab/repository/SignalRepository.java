package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.orders.Signal;

public interface SignalRepository extends JpaRepository<Signal, Long> {
}
