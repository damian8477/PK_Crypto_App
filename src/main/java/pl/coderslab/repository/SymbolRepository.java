package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.orders.Symbol;

public interface SymbolRepository extends JpaRepository<Symbol, Integer> {
}
