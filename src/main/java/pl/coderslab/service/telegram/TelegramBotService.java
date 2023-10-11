package pl.coderslab.service.telegram;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {
    private final RequestTelegramService requestTelegramService;

    @Override
    public String getBotUsername() {
//        return System.getenv("TELEGRAM_NAME").getBytes().toString();
        return requestTelegramService.botName;
    }

    @Override
    public String getBotToken() {
//        return System.getenv("TELEGRAM_TOKEN").getBytes().toString();
        return requestTelegramService.token;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getText()    );
        String message = update.getMessage().getText();
        SendMessage response = new SendMessage();
        response.setChatId(update.getMessage().getChatId().toString());
        String chatId = update.getMessage().getChatId().toString();
        String mess = requestTelegramService.newMessege(chatId, message);
        //todo obsluzyc logowanie do telegrama, jakos,,,  może pin ustawiany dla uzytkownika i po podaniu loginu i pinu numer czasu zostanie dopisany
        //do listy czatów uzytkownika (możliwość usuniecia czatu z poziomu telegrama, mozliwosc zalogowania sie na nowego telegrama, ale nadpisujac starego
        //narazie tylko powiadomienia o otwieraniu i zamykaniu zlecen
        response.setText(mess);
        try{
            execute(response);
        }catch (TelegramApiException e){
            System.out.println(e);//todo logger
        }
    }

    @SneakyThrows
    public void sendMessage(String chatId, String message) {
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        response.setText(message);
        try{
            execute(response);
        }catch (TelegramApiException e){
            try{
                String[] messTab = message.toString().split("------");
                for(String outPart : messTab){
                    response.setText(outPart);
                    execute(response);
                }
                System.out.println(e);
            } catch (Exception f){
                for (int i = 0; i < message.length(); i += 1500) {
                    int next = i + 1500;
                    if(next > message.length()) next = message.length();
                    String mess = message.substring(i, next);
                    response.setText(mess);
                    execute(response);
                }
                System.out.println(f);
            }

        }
    }
}
