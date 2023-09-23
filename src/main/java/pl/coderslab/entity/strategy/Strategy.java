package pl.coderslab.entity.strategy;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.repository.cdi.Eager;
import pl.coderslab.entity.user.User;

import java.util.List;

@Entity
@Table(name = "strategies")
@Data
public class Strategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "percent_of_money")
    private Double percentOfMoney;
    private boolean active;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_strategy",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "strategy_id"))
    private List<User> users;

}
