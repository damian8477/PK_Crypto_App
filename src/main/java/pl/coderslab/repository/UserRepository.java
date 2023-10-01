package pl.coderslab.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.entity.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

}
