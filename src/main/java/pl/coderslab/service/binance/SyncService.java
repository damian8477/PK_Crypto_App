package pl.coderslab.service.binance;

import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.RequestOptions;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.constants.Constants;
import pl.coderslab.entity.user.UserSetting;

import static java.util.Objects.isNull;

@Service
public class SyncService {

    public final SyncRequestClient syncRequestClient;

    public SyncService(){
        syncRequestClient = sync(null);
    }

    public UserSetting defaultUser(){
        return UserSetting.builder().binanceKey(Constants.API_KEY).binanceSecret(Constants.SECRET_KEY).build();
    }

    public SyncRequestClient sync(UserSetting userSetting) {
        try {
            if(isNull(userSetting)){
                userSetting = defaultUser();
            }
            RequestOptions options = new RequestOptions();
            return SyncRequestClient.create(userSetting.getBinanceKey(), userSetting.getBinanceSecret(), options);
        } catch (Exception e) {
            return sync(userSetting);
        }
    }

}
