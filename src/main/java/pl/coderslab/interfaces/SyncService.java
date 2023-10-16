package pl.coderslab.interfaces;

import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.entity.user.UserSetting;

public interface SyncService {
    SyncRequestClient sync(UserSetting userSetting);
}
