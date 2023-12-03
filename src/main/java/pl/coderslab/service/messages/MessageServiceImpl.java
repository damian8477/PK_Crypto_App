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
}
