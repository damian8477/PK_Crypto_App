package pl.coderslab.service.parser;

import org.springframework.stereotype.Service;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.model.CommonSignal;

import java.util.ArrayList;
import java.util.List;

@Service
public class VipSignalParserService {
    public CommonSignal parseSignalMessage(String message) {
        return CommonSignal.builder()
                .sourceName("vip")
                .symbol(getCryptoName(message))
                .takeProfit(getTakeProfit(message))
                .takeProfitAgainst(getTakeProfit(message))
                .lever(10)
                .stopLoss(List.of(getStopLoss(message)))
                .entryPrice(getEntryPriceList(message))
                .orderType(OrderType.MARKET)
                .positionSide(positionSide(message))
                .isStrategy(true)
                .build();
    }
    private String getCryptoName(String message) {
        return message.substring(0, (message.indexOf("USDT") - 1)) + "USDT";
    }
    private List<String> getTakeProfit(String message){
        List<String> takeProfits = new ArrayList<>();
        takeProfits.add(getTakeProfitPrice(message, "TP1 - ", "✅ TP2"));
        takeProfits.add(getTakeProfitPrice(message, "TP2 - ", "✅ TP3"));
        String takeProfit3 = getTakeProfitPrice(message, "TP3 - ", "🚫SL");
        takeProfits.add(takeProfit3);
        takeProfits.add(getTakeProfitGenerate(message, 2, takeProfit3));
        takeProfits.add(getTakeProfitGenerate(message, 4, takeProfit3));
        return takeProfits;
    }
    private String getTakeProfitPrice(String message, String start, String end) {
        String takeProfit1 = message.substring((message.indexOf(start) + 6), (message.indexOf(end) - 1));
        return String.valueOf(Double.parseDouble(takeProfit1));
    }
    private String getTakeProfitGenerate(String message, double procentOfTp3, String takeProfit3) {
        if (message.contains("BUY")) {
            return String.valueOf(aroundValue(takeProfit3, Double.parseDouble(takeProfit3) * ((100.0 + procentOfTp3) / 100)));
        } else {
            return String.valueOf(aroundValue(takeProfit3, Double.parseDouble(takeProfit3) * ((100.0 - procentOfTp3) / 100)));
        }
    }
    private String getStopLoss(String message) {
        return message.substring((message.indexOf("🚫SL") + 5), message.indexOf("📊Lev") - 2);
    }
    private List<String> getEntryPriceList(String message){
        List<String> entryPrices = new ArrayList<>();
        entryPrices.add(String.valueOf(getEntryPrice(message, 1)));
        entryPrices.add(String.valueOf(getEntryPrice(message, 2)));
        entryPrices.add(String.valueOf(getEntryPrice(message, 3)));
        return entryPrices;
    }
    private Double getEntryPrice(String message, int number) {
        String enterPrice = "";
        double stopLoss = Double.parseDouble(getStopLoss(message));
        if (message.contains("BUY")) {
            enterPrice = clearDetails(message.substring((message.indexOf("Enter above:") + 12), message.indexOf("✅") - 2));
            double sub = Double.parseDouble(enterPrice) - stopLoss;
                switch (number){
                    case 1 :
                        return Double.parseDouble(enterPrice);
                    case 2 :
                        double div = sub / 3.0;
                        div = aroundValue(enterPrice, div);
                        return aroundValue(enterPrice, (Double.parseDouble(enterPrice) - div));
                    case 3 :
                        double div2 = sub / 3.0 * 2;
                        div2 = aroundValue(enterPrice, div2);
                        return aroundValue(enterPrice, (Double.parseDouble(enterPrice) - div2));
                    default:
                }
        } else if (message.contains("SELL")) {
            enterPrice = clearDetails(message.substring((message.indexOf("Enter below:") + 12), message.indexOf("✅") - 2));
            double sub = stopLoss - Double.parseDouble(enterPrice);
            switch (number){
                case 1 :
                    return Double.parseDouble(enterPrice);
                case 2 :
                    double div = sub / 3.0;
                    div = aroundValue(enterPrice, div);
                    return (Double.parseDouble(enterPrice) + div);
                case 3 :
                    double div2 = sub / 3.0 * 2;
                    div2 = aroundValue(enterPrice, div2);
                    return (Double.parseDouble(enterPrice) + div2);
                default:
            }
        }
        return Double.parseDouble(enterPrice);
    }

    private double aroundValue(String enterPrice, double div) {
        String[] result = enterPrice.split("\\.");
        String part2 = result[1];
        double precision;
        if (!part2.isEmpty()) {
            precision = Math.pow(10.0, part2.length());
        } else {
            precision = 10.0;
        }
        div = Math.round(div * precision);
        div = div / precision;
        return div;
    }

    private PositionSide positionSide(String message) {
        if (message.contains("BUY")) {
            return PositionSide.LONG;
        } else if (message.contains("SELL")) {
            return PositionSide.SHORT;
        }
        return null;
    }

    private String clearDetails(String detail) {
        return detail.replaceAll("[ $*]", "").replaceAll("[^\\d.]", "");
    }
}
