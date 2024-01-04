package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.strategy.Strategy;

public interface StrategyRepository extends JpaRepository<Strategy, Long> {
}
