package pl.coderslab.entity.alert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.Direction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "alerts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String symbolName;
    @ManyToOne
    @NotNull
    private User user;
    @NotNull
    private PositionSide positionSide;
    @NotNull
    private BigDecimal price;
    @NotNull
    private Direction direction;
}
