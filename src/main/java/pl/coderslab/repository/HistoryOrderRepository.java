package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.entity.orders.HistoryOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface HistoryOrderRepository extends JpaRepository<HistoryOrder, Long> {
    List<HistoryOrder> findAllByUserId(Long userId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("SELECT h  FROM HistoryOrder h join Signal s on s.id = h.signal.id join Source s2 on s2.id = s.source.id where s2.id=:sourceId and h.user.id =:userId")
    List<HistoryOrder> findAllBySourceAndUserId(@Param("sourceId") Integer sourceId, @Param("userId") Long userId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("SELECT h  FROM HistoryOrder h join Signal s on s.id = h.signal.id join Source s2 on s2.id = s.source.id where s2.id=:sourceId and h.user.id =:userId and h.created between :startDate and :stopDate")
    List<HistoryOrder> findAllBySourceAndUserIdDate(@Param("sourceId") Integer sourceId, @Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("stopDate") LocalDateTime stopDate);
}
