package pl.coderslab.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CryptoName {
    private int id;
    private String symbol;
    private BigDecimal markPrice;
    private boolean open;
    private BigDecimal lowPrice;
    private BigDecimal highPrice;
    private BigDecimal dayChangePercent;

}
