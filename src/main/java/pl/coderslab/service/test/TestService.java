package pl.coderslab.service.test;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.*;
import pl.coderslab.binance.client.model.market.ExchangeInfoEntry;
import pl.coderslab.binance.client.model.trade.Order;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.interfaces.SourceService;
import pl.coderslab.interfaces.SyncService;
import pl.coderslab.repository.SourceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {
    private final SyncService syncService;
    private final SourceService sourceService;

//@Scheduled(fixedDelay = 600000, initialDelay = 1000)
    public void test(){
        Source source = sourceService.findByName("VIP");
        System.out.println("kurwa");
        SyncRequestClient syncRequestClient = syncService.sync(null);
    List<Order> listOrder = syncRequestClient.getOpenOrders("BTCUSDT").stream()
            .filter(s -> s.getPositionSide().equals("LONG"))
            .toList();

    for (Order order : listOrder) {
        //if(order.getWorkingType())
        System.out.println(order);
    }
    }

    private void test1(){
        Source source = sourceService.findByName("VIP");
        System.out.println("kurwa");
        SyncRequestClient syncRequestClient = syncService.sync(null);
        syncRequestClient.postOrder("BTCUSDT", OrderSide.BUY, PositionSide.LONG, OrderType.STOP_MARKET, null,
                "0.004", null, null, null, "39000", null, NewOrderRespType.ACK);
        try {
            Thread.sleep(1000);  // Opóźnienie na 1 sekundę
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        syncRequestClient.postOrder("BTCUSDT", OrderSide.SELL, PositionSide.LONG, OrderType.STOP_MARKET, TimeInForce.GTC,
                "0.004", null, null, null, "32000", null, NewOrderRespType.ACK);
        try {
            Thread.sleep(1000);  // Opóźnienie na 1 sekundę
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        syncRequestClient.postOrder("BTCUSDT", OrderSide.SELL, PositionSide.LONG, OrderType.TAKE_PROFIT_MARKET, TimeInForce.GTC,
                "0.004", null, null, null,"40000", null, NewOrderRespType.ACK);
//        System.out.println(syncRequestClient.getExchangeInformation().getSymbols().stream().filter(s->s.getSymbol().equals("BTCUSDT")).collect(Collectors.toList()));

    }
//    [ExchangeInfoEntry[
//            symbol=BTCUSDT,
//    status=TRADING,
//    maintMarginPercent=2.5,
//    requiredMarginPercent=5,
//    baseAsset=BTC,
//    quoteAsset=USDT,
//    pricePrecision=2,
//    quantityPrecision=3,
//    baseAssetPrecision=8,
//    quotePrecision=8,
//    orderTypes=[LIMIT, MARKET, STOP, STOP_MARKET, TAKE_PROFIT, TAKE_PROFIT_MARKET, TRAILING_STOP_MARKET],
//    timeInForce=[LIMIT, MARKET, STOP, STOP_MARKET, TAKE_PROFIT, TAKE_PROFIT_MARKET, TRAILING_STOP_MARKET],
//    filters=[[{minPrice=556.80}, {maxPrice=4529764}, {filterType=PRICE_FILTER}, {tickSize=0.10}], [{stepSize=0.001}, {filterType=LOT_SIZE}, {maxQty=1000}, {minQty=0.001}], [{stepSize=0.001}, {maxQty=120}, {filterType=MARKET_LOT_SIZE}, {minQty=0.001}], [{limit=200}, {filterType=MAX_NUM_ORDERS}], [{limit=10}, {filterType=MAX_NUM_ALGO_ORDERS}], [{notional=100}, {filterType=MIN_NOTIONAL}], [{multiplierDown=0.9500}, {multiplierUp=1.0500}, {multiplierDecimal=4}, {filterType=PERCENT_PRICE}]]]]

}
