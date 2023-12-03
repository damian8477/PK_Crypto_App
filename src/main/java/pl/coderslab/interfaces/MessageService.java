package pl.coderslab.interfaces;

import java.util.Locale;

public interface MessageService {
    String getOrderCloseOwn(Locale locale);

    String getOrderOpenSignal(Locale locale);
}
