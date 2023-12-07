package pl.coderslab.interfaces;

import java.util.Locale;

public interface MessageService {
    String getOrderCloseOwn(Locale locale);

    String getOrderOpenSignal(Locale locale);

    String getTelegramUserAuthError(Locale locale);

    String getTelegramUserAuthConfirm(Locale locale);

    String getTelegramUserLogout(Locale locale);

    String getTelegramUserWait(Locale locale);

    String getTelegramUserError(Locale locale);

    String getTelegramUserNotAuthorized(Locale locale);
}
