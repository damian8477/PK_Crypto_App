package pl.coderslab.service.telegram.vip;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pl.coderslab.configuration.properties.VipSignalConfigProperties;

@Service
@RequiredArgsConstructor
public class TelegramVipServiceImpl extends TelegramLongPollingBot {
    private final RequestSignalVipServiceImpl requestSignalVipService;
    private final VipSignalConfigProperties vipSignalConfigProperties;
    private static final Logger logger = LoggerFactory.getLogger(TelegramVipServiceImpl.class);

    @Override
    public String getBotUsername() {
        return vipSignalConfigProperties.getName();
    }

    @Override
    public String getBotToken() {
        return vipSignalConfigProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        SendMessage response = new SendMessage();
        response.setChatId(update.getMessage().getChatId().toString());
        requestSignalVipService.newMessage(message);
        response.setText("Read ok!");
        try {
            execute(response);
        } catch (TelegramApiException e) {
            logger.info(e.toString());
        }
    }
}
