package pl.coderslab.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pl.coderslab.TestFixtures;
import pl.coderslab.entity.alert.Alert;
import pl.coderslab.entity.user.User;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

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
        User user = TestFixtures.user();
        user.setUsername("testuser");
        entityManager.persist(user);
        alert.setUser(user);
        entityManager.persist(alert);
        entityManager.flush();

        alertRepository.delete(alert);

        assertFalse(alertRepository.existsById(alert.getId()));
    }

    @Test
    void deleteAllByUserShouldBeAbleToRemoveAllAlertForUser(){
        Alert alert = TestFixtures.alert();
        Alert alert1 = alert;
        Alert alert2 = alert;
        alert2.setSymbolName("ETHUSDT");
        User user = TestFixtures.user();
        user.setUsername("testuser");
        entityManager.persist(user);
        alert1.setUser(user);
        alert2.setUser(user);
        entityManager.persist(alert1);
        entityManager.persist(alert2);
        entityManager.flush();

        alertRepository.deleteAllByUserId(user.getId());

        List<Alert> alerts = alertRepository.findAll();
        assertFalse(alerts.stream().anyMatch(s->s.getUser().getId().equals(user.getId())));
    }

}