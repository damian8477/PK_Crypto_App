package pl.coderslab.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.configuration.DatabaseConverter;
import pl.coderslab.enums.TokenType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_token")
@Builder @NoArgsConstructor @AllArgsConstructor
@Data
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Convert(converter = DatabaseConverter.class)
    private String token;
    @Column(name = "token_type")
    private TokenType tokenType;
    private LocalDateTime created;

    @ManyToOne
    private User user;

    @PrePersist
    public void prePersis() {
        created = LocalDateTime.now();
    }
}
