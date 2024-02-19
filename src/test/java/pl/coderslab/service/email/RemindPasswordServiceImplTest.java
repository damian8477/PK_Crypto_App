package pl.coderslab.service.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.coderslab.TestFixtures;
import pl.coderslab.entity.user.User;
import pl.coderslab.interfaces.EmailService;
import pl.coderslab.interfaces.MessageService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.interfaces.UserTokenService;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class RemindPasswordServiceImplTest {

    @InjectMocks
    RemindPasswordServiceImpl remindPasswordService;
    @Mock
    private EmailService emailService;
    @Mock
    private UserService userService;
    @Mock
    private UserTokenService userTokenService;
    @Mock
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void remindPassword_userNotFound_Email(){
        when(userService.getUserByEmail("email")).thenReturn(null);

        remindPasswordService.remindPassword("email", Locale.UK);

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void remindPassword_UserFound_EmailSent(){
        String email = "test@wp.pl";
        User user = TestFixtures.user();
        when(userService.getUserByEmail(email)).thenReturn(user);
        when(userTokenService.generateUserTokenRemindPassword(user)).thenReturn("token");
        when(messageService.getEmailRemindPasswordSubject(Locale.ENGLISH)).thenReturn("Test message");

        remindPasswordService.remindPassword(email, Locale.ENGLISH);

        verify(userTokenService, times(1)).generateUserTokenRemindPassword(user);
        verify(emailService, times(1)).sendEmail(email, "PkCryptoApp", "Test message");
    }
}