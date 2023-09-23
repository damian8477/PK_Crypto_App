package pl.coderslab.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.strategy.Strategy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 5)
    @Column(nullable = false)
    private String login;
    @Size(min=4)
    @Column(nullable = false)
    private String password;
    @OneToMany
    @JoinColumn(name = "id_user")
    private List<UserSetting> userSetting;
    @ManyToMany(mappedBy = "users")
    private List<Strategy> strategies = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();
}
