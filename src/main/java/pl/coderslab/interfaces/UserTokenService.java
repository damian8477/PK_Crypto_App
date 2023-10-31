package pl.coderslab.interfaces;

import pl.coderslab.entity.user.User;
import pl.coderslab.enums.TokenType;

import javax.transaction.Transactional;

public interface UserTokenService {

    boolean generateUserToken(User user, TokenType tokenType);

    boolean checkUserToken(User user, String token);
}
