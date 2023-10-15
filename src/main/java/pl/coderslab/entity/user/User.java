package pl.coderslab.entity.user;

import lombok.Data;
import pl.coderslab.entity.alert.Alert;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.strategy.Strategy;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
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
    private String username;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Size(min = 4)
    @Column(nullable = false)
    private String password;
    @Email
    private String email;
    private boolean active;
    private String role = "ROLE_USER";
    @OneToMany(mappedBy = "user")
    private List<UserSetting> userSetting = new ArrayList<>();
    @ManyToMany(mappedBy = "users")
    private List<Strategy> strategies = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Alert> alerts = new ArrayList<>();
}
