package pl.coderslab.service.telegram;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;

@Service
@RequiredArgsConstructor
public class SignalService {

    private static final Logger logger = LoggerFactory.getLogger(RequestTelegramService.class);

//    @Value("${telegram.token.var.signal}")
//    public String signalToken;
//    @Value("${telegram.token.var.name}")
//    public String signalBotName;

    public String newMessage(String chatId, String mess) {
        return "";
    }


}
