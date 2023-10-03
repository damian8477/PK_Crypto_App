package pl.coderslab.entity.orders;

import lombok.Data;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "symbol_name")
    private String symbolName;
    private BigDecimal price;
    private BigDecimal tp;
    private BigDecimal sl;
    private String currentTakeProfit;
    private String currentStopLoss;
    private String currentEntryPrice;
    private String lot;
    private boolean isStrategy;
    private Double profitProcent;
    private String side;
    private String amount;
    private String startProfit;
    private Integer leverage;
    @Column(updatable = false)
    private LocalDateTime created;
    private LocalDateTime updated;

    @ManyToOne
    private Strategy strategy;
    @ManyToOne
    private User user;
    @ManyToOne
    private Signal signal;

    @PrePersist
    public void prePersis(){
        created = LocalDateTime.now();
        updated = created;
    }

    @PreUpdate
    public void preUpdate(){
        updated = LocalDateTime.now();
    }

}
