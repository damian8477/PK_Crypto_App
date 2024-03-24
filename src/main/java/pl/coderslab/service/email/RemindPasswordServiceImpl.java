package pl.coderslab.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.User;
import pl.coderslab.interfaces.EmailService;
import pl.coderslab.interfaces.MessageService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.interfaces.UserTokenService;

import java.util.Locale;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class RemindPasswordServiceImpl {
    private final EmailService emailService;
    private final UserService userService;
    private final UserTokenService userTokenService;
    private final MessageService messageService;

    public void remindPassword(String email, Locale locale) {
        User user = userService.getUserByEmail(email);
        if (!isNull(user)) {
            String token = userTokenService.generateUserTokenRemindPassword(user);
            String message = prepareMessage(token, locale);
            emailService.sendEmail(user.getEmail(), "PkCryptoApp", message);
        }
    }

    private String prepareMessage(String token, Locale locale) {
        return String.format(messageService.getEmailRemindPasswordSubject(locale), token);
    }
}
