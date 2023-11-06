package pl.coderslab.binance.common;

import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.CandlestickInterval;
import pl.coderslab.binance.client.model.market.Candlestick;
import pl.coderslab.binance.client.model.market.ExchangeInfoEntry;
import pl.coderslab.binance.client.model.market.ExchangeInformation;
import pl.coderslab.binance.client.model.trade.AccountBalance;
import pl.coderslab.binance.client.model.trade.MarginLot;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.enums.MarginType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Double.parseDouble;
import static java.util.Objects.isNull;

public class Common {

    public List<String> getLotsTp(int size, double minQty, double lot, int lever, double marketPrice){
        int[] partList = new int[]{10, 6, 2, 1, 1};
        int sum = Arrays.stream(partList).sum();
        int partLastAdd = partList[0];
        List<String> lots = new ArrayList<>();
        double partOfLot = lot / sum;
        double lotsLast = lot;
        for (int i = partList.length - 1; i >= 0; i--) {
            int part = (i == 0) ? partLastAdd : partList[i];
            double lotNew = (i == 0) ? lotsLast : partOfLot * part;
            if(lotNew * marketPrice * lever > 5.0 && (i <= size-1) && i >= 0){
                double lotTp = calculateLot(lotNew, minQty);
                lotsLast -= lotTp;
                lots.add(String.valueOf(lotTp));
            }
        }
        Collections.reverse(lots);
        return lots;
    }
    public double getAmountValue(String symbol, SyncRequestClient syncRequestClient, Strategy strategy) {
        double balance = getUserBalanceDouble(syncRequestClient, symbol);
        if (strategy.getPercentOfMoney() > 0 && strategy.isPercentMoney()) {
            return (strategy.getPercentOfMoney()  / 100.0) * balance;
        } else {
            return strategy.getPercentOfMoney();
        }
    }

    public double getUserBalanceDouble(SyncRequestClient syncRequestClient, String cryptoName) {
        String curr = getCurrency(cryptoName);
        List<AccountBalance> balanceUserList = syncRequestClient.getBalance();
        List<BigDecimal> balanceList = balanceUserList.stream()
                .filter((s -> s.getAsset().equals(curr)))
                .map(AccountBalance::getBalance).toList();
        return Math.round(balanceList.get(0).doubleValue() * 100.0) / 100.0;
    }

    public MarginLot calculateLotSizeQuantityMargin(String symbol, double amountInUSDT, int leverage, SyncRequestClient syncRequestClient, double marketPrice, ExchangeInfoEntry exchangeInfoEntry) {
        try {
            double minLotSize = calculateMinimumLotSize(symbol, syncRequestClient, exchangeInfoEntry);
            double margin = amountInUSDT * (double) leverage;
            double lotSize = margin / marketPrice;
            return new MarginLot("0.0", Double.toString(calculateLot(lotSize, minLotSize)));
        } catch (Exception e) {
            return new MarginLot("0.0", "0.0");
        }
    }

    public Double getMinQty(String symbol, SyncRequestClient syncRequestClient, ExchangeInfoEntry exchangeInfoEntry){
        return calculateMinimumLotSize(symbol, syncRequestClient, exchangeInfoEntry);
    }

    public int getLengthPrice(SyncRequestClient syncRequestClient, String cryptoName){
        List<Candlestick> candlestickList = syncRequestClient.getCandlestick(cryptoName, CandlestickInterval.FIVE_MINUTES, null, null, 2);
        int lengthEnter = candlestickList.get(0).getClose().toString().length();
        if (candlestickList.get(0).getOpen().toString().length() > lengthEnter)
            lengthEnter = candlestickList.get(0).getOpen().toString().length();
        if (candlestickList.get(0).getHigh().toString().length() > lengthEnter)
            lengthEnter = candlestickList.get(0).getHigh().toString().length();
        if (candlestickList.get(0).getLow().toString().length() > lengthEnter)
            lengthEnter = candlestickList.get(0).getLow().toString().length();
        return lengthEnter;
    }
    public String aroundValueCryptoName(SyncRequestClient syncRequestClient, String cryptoName, String div, Integer length) {
        if(isNull(length)){
            length = getLengthPrice(syncRequestClient, cryptoName);
        }
        if (String.valueOf(div).length() > length) {
            return String.valueOf(div).substring(0, length);
        }
        return div;
    }


    public double calculateMinimumLotSize(String symbol, SyncRequestClient syncRequestClient, ExchangeInfoEntry exchangeInfoEntry) {
        Optional<Map.Entry<String, String>>  minQty;
        if(isNull(exchangeInfoEntry)){
            ExchangeInformation exchangeInformation = syncRequestClient.getExchangeInformation();
            List<ExchangeInfoEntry> exchangeInfoEntries = exchangeInformation.getSymbols();

             minQty = exchangeInfoEntries.stream()
                    .filter(obj -> obj.getSymbol().equals(symbol))
                    .map(ExchangeInfoEntry::getFilters)
                    .flatMap(Collection::stream)
                    .flatMap(Collection::stream)
                    .flatMap(s -> s.entrySet().stream())
                    .filter(s -> s.getKey().equals("minQty"))
                    .findFirst();
        } else {
            minQty = exchangeInfoEntry.getFilters().stream()
                    .flatMap(Collection::stream)
                    .flatMap(s -> s.entrySet().stream())
                    .filter(s -> s.getKey().equals("minQty"))
                    .findFirst();
        }

        if (minQty.isPresent()) {
            double value = parseDouble(minQty.get().getValue());
            if (value == 0.001) return 1000.0;
            if (value == 0.01) return 100.0;
            if (value == 0.1) return 10.0;
            if (value == 1) return 1.0;
        }
        return 0.0;
    }

    public double calculateLot(double lotSize, double minLotSize) {
        return Math.round(Math.abs(lotSize) * minLotSize) / minLotSize;
    }

    private String getCurrency(String cryptoName) {
        if (cryptoName.contains("USDT")) {
            return "USDT";
        } else if (cryptoName.contains("BUSD")) {
            return "BUSD";
        }
        return "USDT";
    }
    public int setLeverageF(SyncRequestClient syncRequestClient, int leverage, String cryptoName) {
        try {
            syncRequestClient.changeInitialLeverage(cryptoName, leverage);
            return leverage;
        } catch (Exception e) {
            int leve = leverage - 1;
            if (leve > 0)
                return setLeverageF(syncRequestClient, leve, cryptoName);
        }
        return leverage;
    }

    public int getLeverageSource(List<Integer> leverages) {
        int minNonZeroLeverage = Integer.MAX_VALUE;

        for (int leverage : leverages) {
            if (leverage > 0 && leverage < minNonZeroLeverage) {
                minNonZeroLeverage = leverage;
            }
        }
        return minNonZeroLeverage;
    }

    public void setMarginType(SyncRequestClient syncRequestClient, MarginType marginType, String cryptoName) {
        try {
            syncRequestClient.changeMarginType(cryptoName, marginType.toString());
        } catch (Exception ignored) {
        }
    }


    public String getStringFormat(String str, Object... objects) {
        return String.format(str, objects);
    }

    public String getTimeStamp() {
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedUTC = localNow.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedIST = zonedUTC.withZoneSameInstant(ZoneId.of("Europe/Warsaw"));
        String YOUR_DATE_TIME_PATTERN = "yyyy-MM-dd' 'HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YOUR_DATE_TIME_PATTERN);
        return zonedIST.format(formatter);
    }
}
