package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.strategy.StrategySetting;

public interface StrategySettingRepository extends JpaRepository<StrategySetting, Integer> {

}
