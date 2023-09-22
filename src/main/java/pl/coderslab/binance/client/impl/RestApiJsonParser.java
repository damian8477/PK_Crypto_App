package pl.coderslab.binance.client.impl;


import pl.coderslab.binance.client.impl.utils.JsonWrapper;

@FunctionalInterface
public interface RestApiJsonParser<T> {

    T parseJson(JsonWrapper json);
}
