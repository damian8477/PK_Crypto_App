package pl.coderslab.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.coderslab.interfaces.EmailService;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        if (isNull(to) || isNull(subject) || isNull(body) || to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            throw new IllegalArgumentException("sendEmail", new Throwable("Arguments cannot be null"));
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }
}