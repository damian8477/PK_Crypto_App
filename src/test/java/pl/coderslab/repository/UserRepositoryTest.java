package pl.coderslab.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.coderslab.TestFixtures;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSettingRepository userSettingRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AlertRepository alertRepository;

    @Test
    void findByUsername_ShouldReturnUser() {
        // Given
        User user = TestFixtures.user();
        user.setUsername("testuser");
        entityManager.persist(user);
        entityManager.flush();

        // When
        User found = userRepository.findByUsername("testuser");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void findByUsername_ShouldNotReturnUser() {
        // Given
        User user = TestFixtures.user();
        user.setUsername("username");
        entityManager.persist(user);
        entityManager.flush();

        // When
        User found = userRepository.findByUsername("testuser");

        // Then
        assertThat(found).isNull();
    }

    @Test
    void findAllByActive_ShouldReturnActiveUsers() {
        // Given
        User activeUser1 = TestFixtures.user();
        activeUser1.setUsername("activeUser1");
        activeUser1.setActive(true);
        entityManager.persist(activeUser1);

        User inactiveUser = TestFixtures.user();
        inactiveUser.setUsername("inactiveUser");
        inactiveUser.setActive(false);
        entityManager.persist(inactiveUser);

        User activeUser2 = TestFixtures.user();
        activeUser2.setUsername("activeUser2");
        activeUser2.setActive(true);
        entityManager.persist(activeUser2);

        entityManager.flush();

        // When
        List<User> activeUsers = userRepository.findAllByActive(true);

        // Then
        assertThat(activeUsers).hasSize(2);
        assertThat(activeUsers).extracting(User::getUsername).containsExactlyInAnyOrder("activeUser1", "activeUser2");
    }

    @Test
    void findAllByActive_ShouldNotReturnActiveUsers() {
        // Given
        User activeUser1 = TestFixtures.user();
        activeUser1.setUsername("activeUser1");
        activeUser1.setActive(false);
        entityManager.persist(activeUser1);

        User inactiveUser = TestFixtures.user();
        inactiveUser.setUsername("inactiveUser");
        inactiveUser.setActive(false);
        entityManager.persist(inactiveUser);

        User activeUser2 = TestFixtures.user();
        activeUser2.setUsername("activeUser2");
        activeUser2.setActive(false);
        entityManager.persist(activeUser2);

        entityManager.flush();

        // When
        List<User> activeUsers = userRepository.findAllByActive(true);

        // Then
        assertThat(activeUsers).isEmpty();
    }

    @Test
    void existsByUsername_ShouldReturnTrueIfUsernameExists() {
        // Given
        User user = TestFixtures.user();
        user.setUsername("existingUser");
        entityManager.persist(user);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByUsername("existingUser");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_ShouldReturnFalseIfUsernameDoesNotExist() {
        // When
        boolean exists = userRepository.existsByUsername("nonExistingUser");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void getUserId_ShouldReturnUserId() {
        // Given
        User user = TestFixtures.user();
        user.setUsername("userIdTestUser");
        entityManager.persist(user);
        entityManager.flush();

        // When

        Long userId = userRepository.getUserId("userIdTestUser");

        // Then
        assertThat(userId).isNotNull();
    }

    @Test
    public void givenUserWithoutRelatedTable_whenDeletingUser_userShouldBeRemoved() {
        //Given
        User user = TestFixtures.user();
        UserSetting userSetting = TestFixtures.userSetting(user);
        entityManager.persist(user);
        entityManager.persist(userSetting);
        //todo dodać jakie ordery i alerty

        User userFound = userRepository.findByUsername(user.getUsername());
        userSettingRepository.deleteAllByUserId(userFound.getId());
        alertRepository.deleteAllByUserId(userFound.getId());
        orderRepository.deleteAllByUserId(userFound.getId());
        //todo strategyRepository dorobić jak bedą już strategie

        userRepository.delete(userFound);
        userRepository.flush();

        boolean exists = userRepository.existsByUsername(user.getUsername());
        assertFalse(exists);
    }

}