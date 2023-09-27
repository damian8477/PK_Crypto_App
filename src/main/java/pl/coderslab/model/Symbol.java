package pl.coderslab.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Symbol {
    private String symbol;
    private String markPrice;
    private boolean open;
}
