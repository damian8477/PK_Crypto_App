package pl.coderslab.strategy.service;

import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.Sar;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.CandlestickInterval;
import pl.coderslab.binance.client.model.market.Candlestick;
import pl.coderslab.binance.common.Common;
import pl.coderslab.interfaces.SyncService;
import pl.coderslab.model.CandleParam;
import pl.coderslab.strategy.indicators.EMA;
import pl.coderslab.strategy.indicators.MACD;
import pl.coderslab.strategy.indicators.RSI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class IndicatorsService extends Common {
    private final SyncRequestClient syncRequestClient;

    public IndicatorsService(SyncService syncService) {
        syncRequestClient = syncService.sync(null);
    }


    public double getEMA(int candleTime, String cryptoName, int period) {
        List<Double> candlestickList = candleReturn(syncRequestClient, candleTime, cryptoName, period);
        double markPrice = syncRequestClient.getMarkPrice(cryptoName).get(0).getMarkPrice().doubleValue();
        EMA ema = new EMA(candlestickList, period, true);
        ema.update(markPrice);
        return aroundValue(candlestickList.get(0).toString(), ema.get());
    }

    public double getEMA(int candleTime, String cryptoName, int period, List<Double> candlestickList, SyncRequestClient syncRequestClient, double markPrice) {
        EMA ema = new EMA(candlestickList, period, true);
        ema.update(markPrice);
        return aroundValue(candlestickList.get(0).toString(), ema.get());
    }

    public double getMacd(int candleTime, String cryptoName, int period, int param1, int param2, int param3) {
        List<Double> candlestickList = candleReturn(syncRequestClient, candleTime, cryptoName, period);
        candlestickList.remove(candlestickList.size() - 1);
        MACD macd = new MACD(candlestickList, 12, 26, 9);
        double markPrice = syncRequestClient.getMarkPrice(cryptoName).get(0).getMarkPrice().doubleValue();
        macd.update(markPrice);
        return aroundValue("0.123456", macd.get());
    }

    public double getMacd(int candleTime, String cryptoName, int period, int param1, int param2, int param3, List<Double> candlestickList, SyncRequestClient syncRequestClient) {
        MACD macd = new MACD(candlestickList, param1, param2, param3);
        return aroundValue("0.12345678", macd.get());
    }

    public double getMacdPrev(int candleTime, String cryptoName, int period, int param1, int param2, int param3, List<Double> candlestickList, SyncRequestClient syncRequestClient) {
        candlestickList.remove(candlestickList.size() - 1);
        MACD macd = new MACD(candlestickList, param1, param2, param3);
        return aroundValue("0.12345678", macd.get());
    }

    public double getMacdAct(int candleTime, String cryptoName, int period, int param1, int param2, int param3, List<Double> candlestickList, SyncRequestClient syncRequestClient, double markPrice) {
        MACD macd = new MACD(candlestickList, param1, param2, param3);
        macd.update(markPrice);
        return aroundValue("0.12345678", macd.get());
    }

    public double getMacdPrev(int candleTime, String cryptoName, int period, int param1, int param2, int param3) {
        List<Double> candlestickList = candleReturn(syncRequestClient, candleTime, cryptoName, period);
        candlestickList.remove(candlestickList.size() - 1);
        candlestickList.remove(candlestickList.size() - 1);
        MACD macd = new MACD(candlestickList, 12, 26, 9);
        double markPrice = syncRequestClient.getMarkPrice(cryptoName).get(0).getMarkPrice().doubleValue();
        macd.update(markPrice);
        return aroundValue("0.12345678", macd.get());
    }

    public double getRSI(int candleTime, String cryptoName, int period) {
        List<Double> candlestickList = candleReturn(syncRequestClient, candleTime, cryptoName, period);
        RSI rsi = new RSI(candlestickList, period);
        return aroundValue(candlestickList.get(0).toString(), rsi.get());
    }

    public double getAvrRsi(String cryptoName, int period) {
        double rsi1 = getRSI(1, cryptoName, period);
        double rsi3 = getRSI(3, cryptoName, period);
        double rsi5 = getRSI(5, cryptoName, period);
        double rsi15 = getRSI(15, cryptoName, period);
        double rsi30 = getRSI(30, cryptoName, period);
        double rsi60 = getRSI(60, cryptoName, period);
        double sumRsi = rsi1 + rsi3 * 3 + rsi5 * 3 + rsi15 * 4 + rsi30 * 4 + rsi60 * 5;
        return sumRsi / 20.0;
    }


    public List<Double> candleReturn(SyncRequestClient syncRequestClient, int minute, String cryptoName, int countCandle) {
        CandlestickInterval candlestickInterval = null;

        if (minute == 1) {
            candlestickInterval = CandlestickInterval.ONE_MINUTE;
        } else if (minute == 3) {
            candlestickInterval = CandlestickInterval.THREE_MINUTES;
        } else if (minute == 5) {
            candlestickInterval = CandlestickInterval.FIVE_MINUTES;
        } else if (minute == 15) {
            candlestickInterval = CandlestickInterval.FIFTEEN_MINUTES;
        } else if (minute == 30) {
            candlestickInterval = CandlestickInterval.HALF_HOURLY;
        } else if (minute == 60) {
            candlestickInterval = CandlestickInterval.HOURLY;
        }
        List<Candlestick> candlestickList = syncRequestClient.getCandlestick(cryptoName, candlestickInterval, null, null, 300);
        return candlestickList.stream().map(candle -> candle.getClose().doubleValue()).collect(Collectors.toList());
    }

    public List<Double> candleReturn(int minute, String cryptoName, int countCandle) {
        CandlestickInterval candlestickInterval = null;

        if (minute == 1) {
            candlestickInterval = CandlestickInterval.ONE_MINUTE;
        } else if (minute == 3) {
            candlestickInterval = CandlestickInterval.THREE_MINUTES;
        } else if (minute == 5) {
            candlestickInterval = CandlestickInterval.FIVE_MINUTES;
        } else if (minute == 15) {
            candlestickInterval = CandlestickInterval.FIFTEEN_MINUTES;
        } else if (minute == 30) {
            candlestickInterval = CandlestickInterval.HALF_HOURLY;
        } else if (minute == 60) {
            candlestickInterval = CandlestickInterval.HOURLY;
        }
        List<Candlestick> candlestickList = syncRequestClient.getCandlestick(cryptoName, candlestickInterval, null, null, countCandle);
        return candlestickList.stream().map(candle -> candle.getClose().doubleValue()).collect(Collectors.toList());
    }

    public String getCandleStick(String cryptoName) {
        CandlestickInterval candlestickInterval = null;
        List<Candlestick> candlestickList = syncRequestClient.getCandlestick(cryptoName, CandlestickInterval.ONE_MINUTE, null, null, 2);
        String low = candlestickList.get(0).getLow().toString();
        String open = candlestickList.get(0).getOpen().toString();
        String close = candlestickList.get(0).getClose().toString();
        String high = candlestickList.get(0).getHigh().toString();
        String returnCandle = low;
        if (open.length() > returnCandle.length()) returnCandle = open;
        if (close.length() > returnCandle.length()) returnCandle = close;
        if (high.length() > returnCandle.length()) returnCandle = high;
        return returnCandle;
    }

    private void checkWaitingOrder() {
        List<CandleParam> candleParamList = candleParameter(syncRequestClient, 3, "GMTUSDT", 1000);
        List<CandleParam> candleParamList2 = candleParameter(syncRequestClient, 60, "BTCUSDT", 100);
    }


    private List<CandleParam> candleParameter(SyncRequestClient syncRequestClient, int minute, String cryptoName, int countCandle) {
        CandlestickInterval candlestickInterval = null;

        if (minute == 1) {
            candlestickInterval = CandlestickInterval.ONE_MINUTE;
        } else if (minute == 3) {
            candlestickInterval = CandlestickInterval.THREE_MINUTES;
        } else if (minute == 5) {
            candlestickInterval = CandlestickInterval.FIVE_MINUTES;
        } else if (minute == 15) {
            candlestickInterval = CandlestickInterval.FIFTEEN_MINUTES;
        } else if (minute == 30) {
            candlestickInterval = CandlestickInterval.HALF_HOURLY;
        } else if (minute == 60) {
            candlestickInterval = CandlestickInterval.HOURLY;
        }
        List<CandleParam> candleParamList = new ArrayList<>();
        List<Candlestick> candlestickList = syncRequestClient.getCandlestick(cryptoName, candlestickInterval, null, null, countCandle);
        for (Candlestick candlestick : candlestickList) {
            CandleParam candleParam = new CandleParam(candlestick.getOpen().doubleValue(), candlestick.getLow().doubleValue(), candlestick.getHigh().doubleValue(), candlestick.getClose().doubleValue(), candlestick.getVolume().doubleValue());
            candleParamList.add(candleParam);
        }

        return candleParamList;
    }

    public Map<String, Double> candle1mLowHigh(String cryptoName) {
        List<Candlestick> candlestickList = syncRequestClient.getCandlestick(cryptoName, CandlestickInterval.ONE_MINUTE, null, null, 2);
        double lowPrice = 10000000.0;
        double highPrice = 0.0;
        for (Candlestick candle : candlestickList) {
            if (candle.getLow().doubleValue() < lowPrice) {
                lowPrice = candle.getLow().doubleValue();
            }
            if (candle.getHigh().doubleValue() > highPrice) {
                highPrice = candle.getHigh().doubleValue();
            }
        }
        Map<String, Double> returnList = new HashMap<>();

        returnList.put("Low", lowPrice);
        returnList.put("High", highPrice);

        return returnList;
    }

    private Sar firstCalculate(List<CandleParam> candleParamList) {
        String upTradeDownTrade = "bull";
        String lastUpTradeDownTrade;


        double accFactor = 0.15;
        double acc = 0.1;
        double max = 0.3;

        double pSar = 0.0;
        double lastPSar;
        double ep = 0.0;
        ;
        double lastEp;
        double epMinusPSar = 0.0;

        double lastAccFactor;
        double epMinusPSarMulacc = 0.0;

        int countBear = 0;
        int countBull = 0;
        CandleParam lastCandleParam = null;
        for (CandleParam candleParam : candleParamList) {

            if (lastCandleParam == null) {
                pSar = candleParam.getLowPrice();
                ep = candleParam.getHighPrice();
                epMinusPSar = ep - pSar;
                epMinusPSarMulacc = epMinusPSar * accFactor;
                accFactor = acc;
                if (epMinusPSarMulacc > 0.0) {
                    upTradeDownTrade = "bull";

                } else {
                    upTradeDownTrade = "bear";
                }
                lastCandleParam = candleParam;
            } else {
                lastPSar = pSar;
                lastUpTradeDownTrade = upTradeDownTrade;
                lastEp = ep;
                lastAccFactor = accFactor;


                //UpDownTrade
                if (pSar < candleParam.getHighPrice()) {
                    upTradeDownTrade = "bull";
                } else if (pSar > candleParam.getLowPrice()) {
                    upTradeDownTrade = "bear";
                }
                if (!upTradeDownTrade.equals(lastUpTradeDownTrade)) {
                    if (countBear > 0) {
                        //System.out.println(upTradeDownTrade + " closePrice " + candleParam.getClosePrice() + " candle " + countBear);
                    } else {
                    }
                }
                //EP
                if (upTradeDownTrade.equals("bull") && candleParam.getHighPrice() > lastEp) {
                    ep = candleParam.getHighPrice();
                } else if (upTradeDownTrade.equals("bull") && candleParam.getHighPrice() <= lastEp) {
                    ep = lastEp;
                } else if ((upTradeDownTrade.equals("bear") && candleParam.getLowPrice() < lastEp)) {
                    ep = candleParam.getLowPrice();
                } else if ((upTradeDownTrade.equals("bear") && candleParam.getLowPrice() >= lastEp)) {
                    ep = lastEp;
                }

                //ACCFACTOR
                if (upTradeDownTrade.equals(lastUpTradeDownTrade)) {
                    if (upTradeDownTrade.equals("bull") && ep > lastEp) {
                        accFactor = lastAccFactor + acc;
                    } else if (upTradeDownTrade.equals("bull") && ep <= lastEp) {
                        accFactor = lastAccFactor;
                    } else if (upTradeDownTrade.equals("bear") && ep < lastEp) {
                        accFactor = lastAccFactor + acc;
                    } else if (upTradeDownTrade.equals("bear") && ep >= lastEp) {
                        accFactor = lastAccFactor;
                    }
                    if (accFactor > max) {
                        accFactor = max;
                    }
                }
                epMinusPSar = ep - pSar;
                epMinusPSarMulacc = epMinusPSar * accFactor;
                //psar
                pSar = lastPSar + epMinusPSarMulacc;
                if (upTradeDownTrade.equals("bear")) {
                    countBear += 1;
                    countBull = 0;

                } else {
                    countBull += 1;
                    countBear = 0;

                }
            }
        }
        return new Sar(upTradeDownTrade.equals("bull"), String.valueOf(aroundValue(String.valueOf(candleParamList.get(0).getClosePrice()), pSar)));
    }

    public Sar getSar(int minute, String cryptoName, int countCandle) {
        List<CandleParam> candleParamList = candleParameter(syncRequestClient, minute, cryptoName, countCandle);
        candleParamList.remove(candleParamList.size() - 1);
        return firstCalculate(candleParamList);
    }
}









