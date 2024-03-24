package pl.coderslab.service.entity;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserToken;
import pl.coderslab.enums.TokenType;
import pl.coderslab.interfaces.EmailService;
import pl.coderslab.interfaces.UserTokenService;
import pl.coderslab.repository.UserTokenRepository;

import javax.transaction.Transactional;
import java.util.Random;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {
    private final UserTokenRepository userTokenRepository;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserTokenServiceImpl.class);

    @Override
    @Transactional
    public boolean generateUserToken(User user, TokenType tokenType) {
        if (!isNull(user.getEmail())) {
            String token = generateRandomCode();
            userTokenRepository.deleteAllByUserId(user.getId());
            userTokenRepository.save(UserToken.builder()
                    .tokenType(tokenType)
                    .token(token)
                    .user(user)
                    .build());
            emailService.sendEmail(user.getEmail(), "Token", String.format("Wprowadź poniższy token w aplikacji: \n %s \n \n Jeśli nie chciałeś zmienić hasła, zgłoś się do administratora", token));
            logger.info(String.format("Wygenerowanie tokenu %s i wysłanie wiadomości do %s na adres %s", tokenType.getLabel(), user.getUsername(), user.getEmail()));
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public String generateUserTokenRemindPassword(User user) {
        if (!isNull(user.getEmail())) {
            String token = generateRandomCodeUUID();
            userTokenRepository.deleteAllByUserId(user.getId());
            userTokenRepository.save(UserToken.builder()
                    .tokenType(TokenType.PASSWORD_REMIND)
                    .token(token)
                    .user(user)
                    .build());
            return token;
        }
        return null;
    }

    @Override
    public boolean checkUserToken(User user, String token) {
        UserToken userToken = userTokenRepository.findByUserId(user.getId());
        if (userToken.getTokenType().equals(TokenType.PASSWORD) && (userToken.getToken().equals(token))) {
            userTokenRepository.deleteById(userToken.getId());
            return true;
        }
        return false;
    }

    @Override
    public boolean existsByToken(String token) {
        return userTokenRepository.existsByToken(token);
    }

    @Override
    public UserToken getUserToken(String token) {
        return userTokenRepository.findByToken(token);
    }

    private String generateRandomCodeUUID() {
        return UUID.randomUUID().toString();
    }

    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
        }
        return code.toString();
    }
}
