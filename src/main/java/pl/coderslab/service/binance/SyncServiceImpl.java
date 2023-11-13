package pl.coderslab.service.binance;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.RequestOptions;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.configuration.properties.BinanceConfigProperties;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.interfaces.SyncService;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class SyncServiceImpl implements SyncService {
    private final BinanceConfigProperties binanceConfigProperties;
    private SyncRequestClient syncRequestClient;
    private final Logger logger = LoggerFactory.getLogger(SyncServiceImpl.class);
    @Override
    public SyncRequestClient sync(UserSetting userSetting) {
        try {
            RequestOptions options = new RequestOptions();
            if (syncRequestClient == null) {
                syncRequestClient = SyncRequestClient.create(binanceConfigProperties.getApiKey(), binanceConfigProperties.getSecretKey(), options);
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
