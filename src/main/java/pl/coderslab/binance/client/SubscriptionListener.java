package pl.coderslab.binance.client;


@FunctionalInterface
public interface SubscriptionListener<T> {


    void onReceive(T data);
}
