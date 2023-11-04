package pl.coderslab.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.source.Source;

public interface SourceRepository extends JpaRepository<Source, Long> {
    Source findByName(String name);
}
