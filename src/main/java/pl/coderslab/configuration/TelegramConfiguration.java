package pl.coderslab.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pl.coderslab.service.telegram.RequestTelegramService;
import pl.coderslab.service.telegram.TelegramBotService;

@Configuration
@RequiredArgsConstructor
public class TelegramConfiguration {
    private final RequestTelegramService requestTelegramService;
    @Bean
    public void telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TelegramBotService(requestTelegramService));
    }

}
