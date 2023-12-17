package pl.coderslab.entity.strategy;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "cci_order")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CCIOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    private boolean active = true;
    private boolean open;
    private boolean win;
    @Column(name = "open_time")
    private LocalDateTime openTime;
    @Column(name = "open_price")
    private double openPrice;
    @Column(name = "close_time")
    private LocalDateTime closeTime;
    @Column(name = "close_price")
    private double closePrice;
}
