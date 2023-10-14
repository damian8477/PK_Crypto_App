package pl.coderslab.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.binance.client.model.enums.PositionSide;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertSetting {
    @NotBlank
    private String symbol;
    private BigDecimal marketPrice;
    @NotNull(message = "Wybierz kierunek")
    private PositionSide positionSide1;
    @NotNull(message = "Musisz wpisać przynajmniej jedną cene")
    private BigDecimal alertPrice1;
    private PositionSide positionSide2;
    private BigDecimal alertPrice2;
    private PositionSide positionSide3;
    private BigDecimal alertPrice3;
    private PositionSide positionSide4;
    private BigDecimal alertPrice4;
    private PositionSide positionSide5;
    private BigDecimal alertPrice5;
}
