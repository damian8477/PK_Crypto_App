package pl.coderslab.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

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
}
