package pl.coderslab.binance.client;

import pl.coderslab.binance.client.impl.BinanceApiInternalFactory;
import pl.coderslab.binance.client.model.enums.CandlestickInterval;
import pl.coderslab.binance.client.model.event.*;
import pl.coderslab.binance.client.model.user.UserDataUpdateEvent;

import java.util.List;


public interface SubscriptionClient {

    static SubscriptionClient create() {
        return create(new SubscriptionOptions());
    }


    static SubscriptionClient create(SubscriptionOptions subscriptionOptions) {
        return BinanceApiInternalFactory.getInstance().createSubscriptionClient(subscriptionOptions);
    }


    void unsubscribeAll();


    void subscribeAggregateTradeEvent(String symbol,
                                      SubscriptionListener<AggregateTradeEvent> callback, SubscriptionErrorHandler errorHandler);


    void subscribeMarkPriceEvent(String symbol,
                                 SubscriptionListener<MarkPriceEvent> callback, SubscriptionErrorHandler errorHandler);


    void subscribeCandlestickEvent(String symbol, CandlestickInterval interval,
                                   SubscriptionListener<CandlestickEvent> callback, SubscriptionErrorHandler errorHandler);


    void subscribeSymbolMiniTickerEvent(String symbol,
                                        SubscriptionListener<SymbolMiniTickerEvent> callback, SubscriptionErrorHandler errorHandler);


    void subscribeAllMiniTickerEvent(SubscriptionListener<List<SymbolMiniTickerEvent>> callback, SubscriptionErrorHandler errorHandler);


    void subscribeSymbolTickerEvent(String symbol,
                                    SubscriptionListener<SymbolTickerEvent> callback, SubscriptionErrorHandler errorHandler);


    void subscribeAllTickerEvent(SubscriptionListener<List<SymbolTickerEvent>> callback, SubscriptionErrorHandler errorHandler);


    void subscribeSymbolBookTickerEvent(String symbol,
                                        SubscriptionListener<SymbolBookTickerEvent> callback, SubscriptionErrorHandler errorHandler);


    void subscribeAllBookTickerEvent(SubscriptionListener<SymbolBookTickerEvent> callback, SubscriptionErrorHandler errorHandler);


    void subscribeSymbolLiquidationOrderEvent(String symbol,
                                              SubscriptionListener<LiquidationOrderEvent> callback, SubscriptionErrorHandler errorHandler);


    void subscribeAllLiquidationOrderEvent(SubscriptionListener<LiquidationOrderEvent> callback, SubscriptionErrorHandler errorHandler);


    void subscribeBookDepthEvent(String symbol, Integer limit,
                                 SubscriptionListener<OrderBookEvent> callback, SubscriptionErrorHandler errorHandler);


    void subscribeDiffDepthEvent(String symbol,
                                 SubscriptionListener<OrderBookEvent> callback, SubscriptionErrorHandler errorHandler);


    void subscribeUserDataEvent(String listenKey,
                                SubscriptionListener<UserDataUpdateEvent> callback, SubscriptionErrorHandler errorHandler);


}
