package pl.coderslab.entity.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Entity
@Table(name = "users_trade_settings")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTradeSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Min(0)
    @Max(23)
    @ElementCollection
    @CollectionTable(name = "trade_hours", joinColumns = @JoinColumn(name = "user_trade_setting_id"))
    @Column(name = "hour")
    private List<String> tradeHours;

    @ElementCollection
    @CollectionTable(name = "trade_days", joinColumns = @JoinColumn(name = "user_trade_setting_id"))
    @Column(name = "day")
    private List<String> tradeDays;

    @OneToOne
    private User user;
}
