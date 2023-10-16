package pl.coderslab.interfaces;

public interface TelegramBotService {
    void sendMessage(String chatId, String message);
}
