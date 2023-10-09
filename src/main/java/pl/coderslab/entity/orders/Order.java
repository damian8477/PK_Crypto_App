package pl.coderslab.entity.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
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
    private String side;
    private String amount;
    @Column(name = "start_profit")
    private String startProfit;
    private Integer leverage;
    @Column(name = "app_order")
    private boolean appOrder;
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
    public void prePersis() {
        created = LocalDateTime.now();
        updated = created;
    }

    @PreUpdate
    public void preUpdate() {
        updated = LocalDateTime.now();
    }

}
