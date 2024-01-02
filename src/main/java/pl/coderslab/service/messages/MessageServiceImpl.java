package pl.coderslab.service.messages;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import pl.coderslab.interfaces.MessageService;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageSource messageSource;

    @Override
    public String getOrderCloseOwn(Locale locale) {
        return messageSource.getMessage("order.close.app.telegram", null, locale);
    }
    @Override
    public String getOrderOpenSignal(Locale locale){
        return messageSource.getMessage("order.open.signal.telegram", null, locale);
    }
    @Override
    public String getTelegramUserAuthError(Locale locale){
        return messageSource.getMessage("telegram.user.authorized.error", null, locale);
    }
    @Override
    public String getTelegramUserAuthConfirm(Locale locale){
        return messageSource.getMessage("telegram.user.authorized.confirm", null, locale);
    }
    @Override
    public String getTelegramUserLogout(Locale locale){
        return messageSource.getMessage("telegram.user.logout", null, locale);
    }
    @Override
    public String getTelegramUserWait(Locale locale){
        return messageSource.getMessage("telegram.user.wait", null, locale);
    }
    @Override
    public String getTelegramUserError(Locale locale){
        return messageSource.getMessage("telegram.user.error", null, locale);
    }
    @Override
    public String getTelegramUserNotAuthorized(Locale locale){
        return messageSource.getMessage("telegram.user.not.authorized", null, locale);
    }

    @Override
    public String getEmailRemindPasswordSubject(Locale locale){
        return messageSource.getMessage("email.remind.password.subject", null, locale);
    }
}
