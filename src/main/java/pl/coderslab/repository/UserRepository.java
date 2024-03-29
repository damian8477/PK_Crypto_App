package pl.coderslab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.coderslab.entity.user.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findFirstByEmail(String email);

    List<User> findAllByActive(boolean active);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("select u.id from User u where u.username = ?1")
    Long getUserId(String userName);
}
