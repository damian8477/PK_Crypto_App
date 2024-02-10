package pl.coderslab.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pl.coderslab.TestFixtures;
import pl.coderslab.entity.alert.Alert;
import pl.coderslab.entity.user.User;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AlertRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void deleteAlertShouldBePossible(){
        Alert alert = TestFixtures.alert();
        entityManager.merge(alert);
        entityManager.flush();
        //authorRepository.delete(alert);

        assertTrue(alertRepository.existsById(alert.getId()));

    }

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

}