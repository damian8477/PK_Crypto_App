package pl.coderslab.service.binance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.RequestOptions;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.entity.user.UserSetting;

import static java.util.Objects.isNull;

@Service
public class SyncService {

    @Value("${binance.api.var.api-key}")
    public String apiKey;
    @Value("${binance.api.var.secret-key}")
    public String secretKey;
    public SyncRequestClient syncRequestClient;
    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);

    public SyncRequestClient sync(UserSetting userSetting) {
        try {
            RequestOptions options = new RequestOptions();
            if (syncRequestClient == null) {
                syncRequestClient = SyncRequestClient.create(apiKey, secretKey, options);
            }
            if (isNull(userSetting)) {
                return syncRequestClient;
            }
            return SyncRequestClient.create(userSetting.getBinanceKey(), userSetting.getBinanceSecret(), options);
        } catch (Exception e) {
            return sync(userSetting);
        }
    }

}
