package pl.coderslab.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
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
    @Size(min=4)
    @Column(nullable = false)
    private String password;
    private boolean active;
    private String role;
    @OneToMany
    @JoinColumn(name = "id_user")
    private List<UserSetting> userSetting;
}
