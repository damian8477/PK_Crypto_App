package pl.coderslab.binance.client;

import com.alibaba.fastjson.JSONObject;
import pl.coderslab.binance.client.impl.BinanceApiInternalFactory;
import pl.coderslab.binance.client.model.ResponseResult;
import pl.coderslab.binance.client.model.enums.*;
import pl.coderslab.binance.client.model.market.*;
import pl.coderslab.binance.client.model.trade.*;

import java.util.List;


public interface SyncRequestClient {


    static SyncRequestClient create() {
        return create("", "", new RequestOptions());
    }


    static SyncRequestClient create(String apiKey, String secretKey) {
        return BinanceApiInternalFactory.getInstance().createSyncRequestClient(apiKey, secretKey, new RequestOptions());
    }


    static SyncRequestClient create(String apiKey, String secretKey, RequestOptions options) {
        return BinanceApiInternalFactory.getInstance().createSyncRequestClient(apiKey, secretKey, options);
    }


    ExchangeInformation getExchangeInformation();


    OrderBook getOrderBook(String symbol, Integer limit);


    List<Trade> getRecentTrades(String symbol, Integer limit);


    List<Trade> getOldTrades(String symbol, Integer limit, Long fromId);


    List<AggregateTrade> getAggregateTrades(String symbol, Long fromId, Long startTime, Long endTime, Integer limit);


    List<Candlestick> getCandlestick(String symbol, CandlestickInterval interval, Long startTime, Long endTime, Integer limit);


    List<MarkPrice> getMarkPrice(String symbol);


    List<FundingRate> getFundingRate(String symbol, Long startTime, Long endTime, Integer limit);


    List<PriceChangeTicker> get24hrTickerPriceChange(String symbol);


    List<SymbolPrice> getSymbolPriceTicker(String symbol);


    List<SymbolOrderBook> getSymbolOrderBookTicker(String symbol);


    List<LiquidationOrder> getLiquidationOrders(String symbol, Long startTime, Long endTime, Integer limit);


    List<Object> postBatchOrders(String batchOrders);


    Order postOrder(String symbol, OrderSide side, PositionSide positionSide, OrderType orderType,
                    TimeInForce timeInForce, String quantity, String price, String reduceOnly,
                    String newClientOrderId, String stopPrice, WorkingType workingType, NewOrderRespType newOrderRespType);


    Order cancelOrder(String symbol, Long orderId, String origClientOrderId);


    ResponseResult cancelAllOpenOrder(String symbol);


    List<Object> batchCancelOrders(String symbol, String orderIdList, String origClientOrderIdList);


    ResponseResult changePositionSide(boolean dual);


    ResponseResult changeMarginType(String symbolName, String marginType);


    JSONObject addIsolatedPositionMargin(String symbolName, int type, String amount, PositionSide positionSide);


    List<WalletDeltaLog> getPositionMarginHistory(String symbolName, int type, long startTime, long endTime, int limit);


    JSONObject getPositionSide();


    Order getOrder(String symbol, Long orderId, String origClientOrderId);


    List<Order> getOpenOrders(String symbol);


    List<Order> getAllOrders(String symbol, Long orderId, Long startTime, Long endTime, Integer limit);


    List<AccountBalance> getBalance();


    AccountInformation getAccountInformation();


    Leverage changeInitialLeverage(String symbol, Integer leverage);


    List<PositionRisk> getPositionRisk();


    List<MyTrade> getAccountTrades(String symbol, Long startTime, Long endTime, Long fromId, Integer limit);


    List<Income> getIncomeHistory(String symbol, IncomeType incomeType, Long startTime, Long endTime, Integer limit);


    String startUserDataStream();


    String keepUserDataStream(String listenKey);


    String closeUserDataStream(String listenKey);


    List<OpenInterestStat> getOpenInterestStat(String symbol, PeriodType period, Long startTime, Long endTime, Integer limit);


    List<CommonLongShortRatio> getTopTraderAccountRatio(String symbol, PeriodType period, Long startTime, Long endTime, Integer limit);


    List<CommonLongShortRatio> getTopTraderPositionRatio(String symbol, PeriodType period, Long startTime, Long endTime, Integer limit);


    List<CommonLongShortRatio> getGlobalAccountRatio(String symbol, PeriodType period, Long startTime, Long endTime, Integer limit);


    List<TakerLongShortStat> getTakerLongShortRatio(String symbol, PeriodType period, Long startTime, Long endTime, Integer limit);

}