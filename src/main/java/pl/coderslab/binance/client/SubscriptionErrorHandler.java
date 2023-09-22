package pl.coderslab.binance.client;

import pl.coderslab.binance.client.exception.BinanceApiException;


@FunctionalInterface
public interface SubscriptionErrorHandler {

    void onError(BinanceApiException exception);
}
