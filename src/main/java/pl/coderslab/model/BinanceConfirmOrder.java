package pl.coderslab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class BinanceConfirmOrder {
    private String symbol;
    private BigDecimal realizedPln;
    private BigDecimal commission;
    private String closePrice;
    private String entryPrice;
    private String lot;
    private String time;
}
