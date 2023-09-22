package pl.coderslab.binance.client;

import pl.coderslab.binance.client.constant.BinanceApiConstants;
import pl.coderslab.binance.client.exception.BinanceApiException;

import java.net.URL;


public class RequestOptions {

    private String url = BinanceApiConstants.API_BASE_URL;

    public RequestOptions() {
    }

    public RequestOptions(RequestOptions option) {
        this.url = option.url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        try {
            URL u = new URL(url);
            this.url = u.toString();
        } catch (Exception e) {
            throw new BinanceApiException(BinanceApiException.INPUT_ERROR, "The URI is incorrect: " + e.getMessage());
        }
        this.url = url;
    }
}
