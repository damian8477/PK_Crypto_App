package pl.coderslab.service.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void sendEmail_shouldSendEmail() {
        // Arrange
        String to = "dziuba925@gmail.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // Act
        emailService.sendEmail(to, subject, body);

        // Assert
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }


    @Test
    void sendEmail_withEmptyRecipient_shouldThrowException() {
        // Arrange
        String recipient = "";
        String subject = "Test Subject";
        String body = "Test Body";

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail(recipient, subject, body));
    }

    @Test
    void sendEmail_withEmptySubject_shouldThrowException() {
        // Arrange
        String recipient = "test@example.pl";
        String subject = "";
        String body = "Test Body";

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail(recipient, subject, body));
    }

    @Test
    void sendEmail_withEmptyBody_shouldThrowException() {
        // Arrange
        String recipient = "test@example.pl";
        String subject = "";
        String body = "";

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmail(recipient, subject, body));
    }
}