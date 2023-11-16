package pl.coderslab.entity.strategy.rsi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rsi_strategy")
public class RsiStrategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Pattern(regexp = ".*(?:usdt|USDT)$", message = "Nazwa musi się kończyć na 'usdt' lub 'USDT'")
    private String symbol;
    @Column(name = "long_rsi_trigger")
    private int longRsiTrigger = 20;
    @Column(name = "sell_rsi_trigger")
    private int sellRsiTrigger = 80;
    @Column(name = "countWinTrade")
    private int countWinTrade = 0;
    @Column(name = "count_loss_trade")
    private int countLossTrade = 0;
    @Column(name = "procent_tp")
    private double procentTp = 1.0;
    @Column(name = "procent_sl")
    private double procentSl = 3.0;
    @Column(name = "sum_rsi")
    private String sumRsi;
    private boolean active = false;
    private boolean up = false;
    private boolean down = false;
}
