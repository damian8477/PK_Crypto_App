package pl.coderslab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.entity.orders.Signal;
import pl.coderslab.enums.Action;
import pl.coderslab.enums.MarginType;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonSignal {
    private String sourceName;
    private Signal signalId;
    private String symbol;
    private PositionSide positionSide;
    private List<BigDecimal> entryPrice;
    private List<BigDecimal> takeProfit;
    private List<BigDecimal> takeProfitAgainst;
    private List<BigDecimal> stopLoss;
    private Action action;
    private String lot;
    private int lever;
    private boolean isStrategy;
    private MarginType marginType;
    private OrderType orderType;
}

