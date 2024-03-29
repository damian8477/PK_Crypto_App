package pl.coderslab.entity.orders;

import lombok.*;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "history_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "symbol_name")
    private String symbol;
    private String entry;
    private String close;
    private String lot;
    private String side;
    private BigDecimal amount;
    private Integer leverage;
    private boolean win;
    private BigDecimal commission;
    @Column(name = "own_close")
    private boolean ownClosed = false;
    @Column(name = "realized_pln")
    private BigDecimal realizedPln;
    @Column(name = "profit_procent")
    private BigDecimal profitPercent;
    @Column(name = "open_time")
    private LocalDateTime openTime;
    @Column(updatable = false)
    private LocalDateTime created;
    @ManyToOne
    private Strategy strategy;
    @ManyToOne
    private User user;
    @ManyToOne
    private Signal signal;

    @PrePersist
    public void prePersis() {
        created = LocalDateTime.now();
    }
}
