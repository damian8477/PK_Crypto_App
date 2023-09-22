package pl.coderslab.binance.client.impl;

import pl.coderslab.binance.client.SubscriptionErrorHandler;
import pl.coderslab.binance.client.SubscriptionListener;
import pl.coderslab.binance.client.impl.utils.Handler;

class WebsocketRequest<T> {

    final SubscriptionListener<T> updateCallback;
    final SubscriptionErrorHandler errorHandler;
    String signatureVersion = "2";
    String name;
    Handler<WebSocketConnection> connectionHandler;
    Handler<WebSocketConnection> authHandler = null;
    RestApiJsonParser<T> jsonParser;

    WebsocketRequest(SubscriptionListener<T> listener, SubscriptionErrorHandler errorHandler) {
        this.updateCallback = listener;
        this.errorHandler = errorHandler;
    }
}
