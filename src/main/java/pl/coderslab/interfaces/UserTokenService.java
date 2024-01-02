package pl.coderslab.interfaces;

import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserToken;
import pl.coderslab.enums.TokenType;

import javax.transaction.Transactional;

public interface UserTokenService {

    boolean generateUserToken(User user, TokenType tokenType);

    String generateUserTokenRemindPassword(User user);

    boolean checkUserToken(User user, String token);

    boolean existsByToken(String token);
    UserToken getUserToken(String token);
}
