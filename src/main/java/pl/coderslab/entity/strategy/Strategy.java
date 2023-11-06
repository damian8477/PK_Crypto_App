package pl.coderslab.entity.strategy;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
    private MarginType marginType;
    @ManyToOne
    private Source source;
    @ManyToMany//(fetch = FetchType.EAGER)
    @JoinTable(name = "user_strategy",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "strategy_id"))
    private List<User> users;
}
