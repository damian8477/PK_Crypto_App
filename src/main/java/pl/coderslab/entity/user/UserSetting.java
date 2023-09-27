package pl.coderslab.entity.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.configuration.DatabaseConverter;

import javax.persistence.*;

@Entity
@Table(name = "user_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Convert(converter = DatabaseConverter.class)
    @Column(name = "binance_key")
    private String binanceKey;
    @Convert(converter = DatabaseConverter.class)
    @Column(name = "binance_secret")
    private String binanceSecret;
    private boolean active;
    @Column(name = "active_signal")
    private boolean activeSignal;
    @ManyToOne
    private User user;



}
