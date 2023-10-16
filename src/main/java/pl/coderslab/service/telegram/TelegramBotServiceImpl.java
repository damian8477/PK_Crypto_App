package pl.coderslab.service.telegram;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pl.coderslab.interfaces.TelegramBotService;

@Service
@RequiredArgsConstructor
public class TelegramBotServiceImpl extends TelegramLongPollingBot implements TelegramBotService {
    private final RequestTelegramService requestTelegramService;
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotServiceImpl.class);


    @Override
    public String getBotUsername() {
        return requestTelegramService.botName;
    }

    @Override
    public String getBotToken() {
        return requestTelegramService.token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        SendMessage response = new SendMessage();
        response.setChatId(update.getMessage().getChatId().toString());
        String chatId = update.getMessage().getChatId().toString();
        String mess = requestTelegramService.newMessage(chatId, message);
        response.setText(mess);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            logger.info(e.toString());
        }
    }
    @Override
    public void sendMessage(String chatId, String message){
        try{
            SendMessage response = new SendMessage();
            response.setChatId(chatId);
            response.setText(message);
            try {
                execute(response);
            } catch (TelegramApiException e) {
                try {
                    String[] messTab = message.toString().split("------");
                    for (String outPart : messTab) {
                        response.setText(outPart);
                        execute(response);
                    }
                } catch (Exception f) {
                    for (int i = 0; i < message.length(); i += 1500) {
                        int next = i + 1500;
                        if (next > message.length()) next = message.length();
                        String mess = message.substring(i, next);
                        response.setText(mess);
                        execute(response);
                    }
                }

            }
        }catch (TelegramApiException e){
            logger.error(e.toString());
        }

    }
}
