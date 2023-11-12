package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.entity.strategy.rsi.RsiStrategy;

public interface RsiStrategyRepository extends JpaRepository<RsiStrategy, Long> {
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RsiStrategy c SET c.sumRsi = :sumRsi WHERE c.id = :id")
    int updateRsiFun(@Param("id") int id, @Param("sumRsi") String sumRsi);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RsiStrategy c SET c.countWinTrade = :countWinTrade WHERE c.id = :id")
    int updateCountWin(@Param("id") int id, @Param("countWinTrade") int countWinTrade);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RsiStrategy c SET c.countLossTrade = :countLossTrade WHERE c.id = :id")
    int updateCountLoss(@Param("id") int id, @Param("countLossTrade") int countLossTrade);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RsiStrategy c SET c.active = :active WHERE c.id = :id")
    int updateActive(@Param("id") int id, @Param("active") boolean active);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RsiStrategy c SET c.up = :up WHERE c.id = :id")
    int updateUp(@Param("id") int id, @Param("up") boolean up);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RsiStrategy c SET c.down = :down WHERE c.id = :id")
    int updateDown(@Param("id") int id, @Param("down") boolean down);


    RsiStrategy findBySymbol(String symbol);

}
