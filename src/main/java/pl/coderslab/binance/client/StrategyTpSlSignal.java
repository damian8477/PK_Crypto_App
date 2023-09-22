package pl.coderslab.binance.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StrategyTpSlSignal {

    private String symbol;
    private String orderSide;
    private String tp;
    private String sl;
    private String phase;
    private int sourceId;
    private String state;
}
