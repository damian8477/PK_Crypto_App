package pl.coderslab.configuration;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pl.coderslab.configuration.properties.InfoBotConfigProperties;
import pl.coderslab.configuration.properties.TokenConfigProperties;
import pl.coderslab.configuration.properties.VipSignalConfigProperties;
import pl.coderslab.service.telegram.RequestTelegramService;
import pl.coderslab.service.telegram.TelegramBotServiceImpl;
import pl.coderslab.service.telegram.TelegramInfoServiceImpl;
import pl.coderslab.service.telegram.vip.RequestSignalVipServiceImpl;
import pl.coderslab.service.telegram.vip.TelegramVipServiceImpl;
import pl.coderslab.strategy.service.Strategy110Service;

@Configuration
@RequiredArgsConstructor
public class TelegramConfiguration {

    private final RequestTelegramService requestTelegramService;
    private final TokenConfigProperties tokenConfigProperties;
    private final VipSignalConfigProperties vipSignalConfigProperties;
    private final RequestSignalVipServiceImpl requestSignalVipService;
    private final InfoBotConfigProperties infoBotConfigProperties;

    private static final Logger logger = LoggerFactory.getLogger(TelegramConfiguration.class);
    @Bean
    public void telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TelegramBotServiceImpl(requestTelegramService, tokenConfigProperties));
        logger.info("telegramBotApi()");
    }

    @Bean
    public void telegramVip() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TelegramVipServiceImpl(requestSignalVipService, vipSignalConfigProperties));
        logger.info("telegramVip()");
    }

    @Bean
    public void telegramInfo() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TelegramInfoServiceImpl(requestTelegramService, infoBotConfigProperties));
        logger.info("telegramInfo()");
    }

}
