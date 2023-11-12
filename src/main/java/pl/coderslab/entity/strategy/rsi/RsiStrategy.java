package pl.coderslab.entity.strategy.rsi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "rsi_strategy")
public class RsiStrategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String symbol;
    @Column(name = "long_rsi_trigger")
    private int longRsiTrigger;
    @Column(name = "sell_rsi_trigger")
    private int sellRsiTrigger;
    @Column(name = "countWinTrade")
    private int countWinTrade;
    @Column(name = "count_loss_trade")
    private int countLossTrade;
    @Column(name = "procent_tp")
    private int procentTp;
    @Column(name = "procent_sl")
    private int procentSl;
    @Column(name = "sum_rsi")
    private String sumRsi;
    private boolean active;
    private boolean up;
    private boolean down;

    public RsiStrategy() {
    }
}
