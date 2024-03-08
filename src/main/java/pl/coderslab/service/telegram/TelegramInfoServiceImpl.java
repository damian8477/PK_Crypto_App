package pl.coderslab.service.telegram;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pl.coderslab.configuration.properties.InfoBotConfigProperties;
import pl.coderslab.configuration.properties.TokenConfigProperties;
import pl.coderslab.interfaces.TelegramBotService;

@Service
@RequiredArgsConstructor
public class TelegramInfoServiceImpl extends TelegramLongPollingBot implements TelegramBotService {
    private final RequestTelegramService requestTelegramService;
    private final InfoBotConfigProperties tokenConfigProperties;
    private static String chatId;
    private static final Logger logger = LoggerFactory.getLogger(TelegramInfoServiceImpl.class);

    @Override
    public String getBotUsername() {
        return tokenConfigProperties.getName();
    }

    @Override
    public String getBotToken() {
        return tokenConfigProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        SendMessage response = new SendMessage();
        response.setChatId(update.getMessage().getChatId().toString());
        String chatId = update.getMessage().getChatId().toString();
        if(message.contains("9513")){
            this.chatId = chatId;
            response.setText("Chat is added");
            sendMessage(null, "sendMessage is work");
        }
        try {
            execute(response);
        } catch (TelegramApiException e) {
            logger.info(e.toString());
        }
    }

    @Override
    public void sendMessage(String chatId, String message) {
        try {
            if(this.chatId == null){
                this.chatId = "2048963965";
            }
                SendMessage response = new SendMessage();
                response.setChatId(this.chatId);
                if (message.length() < 1500) {
                    response.setText(message);
                    execute(response);
                } else {
                    for (int i = 0; i < message.length(); i += 1500) {
                        int next = i + 1500;
                        if (next > message.length()) next = message.length();
                        String mess = message.substring(i, next);
                        response.setText(mess);
                        execute(response);
                    }
                }
        } catch (TelegramApiException e) {
            logger.error(e.toString());
        }
    }
}
