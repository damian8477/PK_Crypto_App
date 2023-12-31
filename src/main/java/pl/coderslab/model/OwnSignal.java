package pl.coderslab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.enums.CashType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnSignal {
    private Strategy strategySetting;
    private UserSetting userSetting;
    private String symbol;
    private PositionSide positionSide = PositionSide.LONG;
    private OrderType typeOrder = OrderType.MARKET;
    private BigDecimal entryPrice;
    private BigDecimal takeProfit;
    private boolean tpPercent;
    private BigDecimal stopLoss;
    private boolean slPercent;
    private CashType cashType;
    private BigDecimal lot;
    private BigDecimal amount;
    private BigDecimal percentOfAccount;
    private int lever = 10;

    public OwnSignal(String symbol) {
        this.symbol = symbol;
    }
}
