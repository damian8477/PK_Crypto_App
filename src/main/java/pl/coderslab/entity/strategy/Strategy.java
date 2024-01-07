package pl.coderslab.entity.strategy;

import lombok.Getter;
import lombok.Setter;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.MarginType;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "strategies")
@Getter
@Setter
public class Strategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "percent_of_money")
    private Double percentOfMoney;
    @Column(name = "percent_money")
    private boolean percentMoney;
    private boolean active;
    @Column(name = "max_leverage")
    private int maxLeverage;
    @Column(name = "margin_type")
    private MarginType marginType;
    @Column(name = "max_count_orders")
    private Integer maxCountOrders;
    @ManyToOne
    private Source source;
    @ManyToOne
    private User user;
    @ManyToMany
    private List<Symbol> symbols;

}
