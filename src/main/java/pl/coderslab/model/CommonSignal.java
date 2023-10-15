package pl.coderslab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.binance.client.model.enums.OrderSide;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.enums.Action;
import pl.coderslab.enums.MarginType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonSignal {
    private String signalId;
    private String symbol;
    private PositionSide positionSide;
    private String entryPrice;
    private List<String> takeProfit;
    private List<String> stopLoss;
    private Action action;
    private String lot;
    private int lever;
    private boolean isStrategy;
    private MarginType marginType;
    private OrderType orderType;
}

