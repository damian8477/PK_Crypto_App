package pl.coderslab.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.coderslab.entity.user.User;

import java.awt.print.Book;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    List<User> findAllByActive(boolean active);

    boolean existsByUsername(String username);

    @Query("select u.id from User u where u.username = ?1")
    Long getUserId(String userName);

    @EntityGraph(attributePaths = {"userSetting", "strategies", "orders", "alerts"})
    User findByFirstName(String firstName);

//    @Query("SELECT DISTINCT u FROM User u " +
//            "LEFT JOIN FETCH u.userSetting " +
//            "LEFT JOIN FETCH u.strategies " +
//            "LEFT JOIN FETCH u.orders " +
//            "LEFT JOIN FETCH u.alerts")
//    List<User> findAllFetch();
//
//
//    @Query("SELECT DISTINCT u FROM User u " +
//            "LEFT JOIN FETCH u.userSetting " +
//            "LEFT JOIN FETCH u.strategies " +
//            "LEFT JOIN FETCH u.orders " +
//            "LEFT JOIN FETCH u.alerts " +
//            "WHERE u.id = :userId")
//    User getUserWithAssociations(@Param("userId") Long userId);
}
