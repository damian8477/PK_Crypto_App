package pl.coderslab.binance.client;

//import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StrategySignal {
   // @JsonProperty("side")
    public String side;

  //  @JsonProperty("symbol")
    public String symbol;
}
