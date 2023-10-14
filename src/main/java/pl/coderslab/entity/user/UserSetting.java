package pl.coderslab.entity.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import pl.coderslab.configuration.DatabaseConverter;

import javax.persistence.*;
import javax.validation.constraints.Size;


@Entity
@Table(name = "user_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Convert(converter = DatabaseConverter.class)
    @Column(name = "binance_key")
    @Size(min = 64, max = 64, message = "Binance key musi mieć 64 znaki")
    private String binanceKey;
    @Convert(converter = DatabaseConverter.class)
    @Column(name = "binance_secret")
    @Size(min = 64, max = 64, message = "Binance secret musi mieć 64 znaki")
    private String binanceSecret;
    @Column(name = "telegram_chat_id")
    @Convert(converter = DatabaseConverter.class)
    private String telegramChatId;
    private boolean active;
    @Column(name = "active_signal")
    private boolean activeSignal;
    @ManyToOne
    private User user;


}
