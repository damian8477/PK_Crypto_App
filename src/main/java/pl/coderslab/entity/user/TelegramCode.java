package pl.coderslab.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.configuration.DatabaseConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "telegram_key")
@Entity
@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class TelegramCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Convert(converter = DatabaseConverter.class)
    @Column(name = "number_code")
    private String numberCode;
    @Column(name = "try_count")
    private int tryCount = 0;
    private LocalDateTime created;

    @OneToOne
    private User user;

    @PrePersist
    public void prePersis() {
        created = LocalDateTime.now();
    }

}
