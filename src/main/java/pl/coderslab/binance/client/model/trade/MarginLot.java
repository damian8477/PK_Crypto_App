package pl.coderslab.binance.client.model.trade;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MarginLot {
    private String margin;
    private String lot;
}
