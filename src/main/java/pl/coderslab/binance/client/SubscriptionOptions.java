package pl.coderslab.binance.client;

import pl.coderslab.binance.client.exception.BinanceApiException;

import java.net.URI;


public class SubscriptionOptions {

    private String uri = "wss://api.binance.pro/";
    private boolean isAutoReconnect = true;
    private int receiveLimitMs = 300_000;
    private int connectionDelayOnFailure = 15;

    public SubscriptionOptions(SubscriptionOptions options) {
        this.uri = options.uri;
        this.isAutoReconnect = options.isAutoReconnect;
        this.receiveLimitMs = options.receiveLimitMs;
        this.connectionDelayOnFailure = options.connectionDelayOnFailure;
    }

    public SubscriptionOptions() {
    }

    public boolean isAutoReconnect() {
        return isAutoReconnect;
    }

    public SubscriptionOptions setAutoReconnect(boolean isAutoReconnect) {
        this.isAutoReconnect = isAutoReconnect;
        return this;
    }

    public int getReceiveLimitMs() {
        return receiveLimitMs;
    }

    public void setReceiveLimitMs(int receiveLimitMs) {
        this.receiveLimitMs = receiveLimitMs;
    }

    public int getConnectionDelayOnFailure() {
        return connectionDelayOnFailure;
    }

    public void setConnectionDelayOnFailure(int connectionDelayOnFailure) {
        this.connectionDelayOnFailure = connectionDelayOnFailure;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        try {
            URI u = new URI(uri);
            this.uri = u.toString();
        } catch (Exception e) {
            throw new BinanceApiException(BinanceApiException.INPUT_ERROR, "The URI is incorrect: " + e.getMessage());
        }
        this.uri = uri;
    }
}
