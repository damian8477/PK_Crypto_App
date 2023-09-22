package pl.coderslab.binance.client;


@FunctionalInterface
public interface ResponseCallback<T> {


    void onResponse(T response);
}
