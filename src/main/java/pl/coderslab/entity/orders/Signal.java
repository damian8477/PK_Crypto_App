package pl.coderslab.entity.orders;

import lombok.*;
import pl.coderslab.entity.source.Source;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "signals")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Signal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    @Column(name = "entry_price")
    private BigDecimal entryPrice;
    @Column(name = "entry_price2")
    private BigDecimal entryPrice2;
    @Column(name = "entry_price3")
    private BigDecimal entryPrice3;
    @Column(name = "take_profit_1")
    private BigDecimal takeProfit1;
    @Column(name = "take_profit_2")
    private BigDecimal takeProfit2;
    @Column(name = "take_profit_3")
    private BigDecimal takeProfit3;
    @Column(name = "take_profit_4")
    private BigDecimal takeProfit4;
    @Column(name = "take_profit_15")
    private BigDecimal takeProfit5;
    @Column(name = "stop_loss")
    private BigDecimal stopLoss;
    @Column(name = "during_time")
    private String duringTime;
    @Column(updatable = false)
    private LocalDateTime created;
    private LocalDateTime updated;
    @ManyToOne
    private Source source;

    @PrePersist
    public void prePersis() {
        created = LocalDateTime.now();
        updated = created;
    }

    @PreUpdate
    public void preUpdate() {
        updated = LocalDateTime.now();
    }

}
