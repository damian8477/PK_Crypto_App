package pl.coderslab.entity.orders;

import jakarta.persistence.*;
import lombok.Data;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;

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
    @Column(updatable = false)
    private LocalDateTime created;
    private LocalDateTime updated;

    @ManyToOne
    private Strategy strategy;
    @ManyToOne
    private User user;

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
