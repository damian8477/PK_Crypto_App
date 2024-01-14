package pl.coderslab.entity.orders;

import lombok.*;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class  Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "symbol_name")
    private String symbolName;
    private String tp;
    private String sl;
    private String entry;
    private String lot;
    @Column(name = "is_strategy")
    private boolean isStrategy;
    @Column(name = "profit_procent")
    private Double profitProcent;
    private PositionSide positionSide;
    private String amount;
    @Column(name = "start_profit")
    private String startProfit;
    private Integer leverage;
    private boolean open;
    @Column(name = "app_order")
    private boolean appOrder;
    private boolean be;
    @Column(updatable = false)
    private LocalDateTime created;
    private LocalDateTime updated;
    @ManyToOne
    private Source source;
    @ManyToOne
    private Strategy strategy;
    @ManyToOne
    private User user;
    @ManyToOne
    private Signal signal;

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
