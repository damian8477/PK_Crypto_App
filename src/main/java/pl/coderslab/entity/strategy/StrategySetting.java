package pl.coderslab.entity.strategy;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "strategy_setting")
@Data
public class StrategySetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String name;
    @Size(min=10)
    private String description;
    @Column(name = "admin_strategy")
    private boolean adminStrategy;
    @Column(name = "be_percent")
    private BigDecimal bePercent;
    @Column(name = "walking_stop_loss")
    private boolean walkingStopLoss;
    @Column(name = "active_basic_tp")
    private boolean activeBasicTp;
    @Column(name = "active_basic_sl")
    private boolean activeBasicSl;
    @Column(name = "basic_tp_percent")
    private BigDecimal basicTpPercent;
    @Column(name = "basic_sl_percent")
    private BigDecimal basicSlPercent;
    @OneToMany
    private List<Strategy> strategies;
//    @ManyToMany
//    @JoinTable(name = "indicators_strategysettings",
//            joinColumns = @JoinColumn(name = "indicator_id"),
//            inverseJoinColumns = @JoinColumn(name = "strategy_setting_id"))
//    private List<Indicator> indicators;


}
