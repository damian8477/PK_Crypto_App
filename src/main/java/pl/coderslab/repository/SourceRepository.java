package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.strategy.Source;

public interface SourceRepository extends JpaRepository<Source, Integer> {
    Source findByName(String name);
    Source findById(int id);
}
