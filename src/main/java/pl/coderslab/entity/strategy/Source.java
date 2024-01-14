package pl.coderslab.entity.strategy;

import lombok.Getter;
import lombok.Setter;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.enums.SourceType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "sources")
@Getter
@Setter
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String name;
    @Size(min = 10)
    private String description;
    private boolean active;
    @Column(name = "source_type")
    private SourceType sourceType;
    private boolean sliding;
    @Column(name = "max_leverage")
    private int maxLeverage;
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
    @OneToMany(fetch = FetchType.EAGER)
    private List<Strategy> strategies;
    @ManyToMany
    private List<Symbol> symbols;

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
    }

    public void deleteSymbol(Symbol symbol) {
        symbols.stream()
                .filter(s -> s.getName().equals(symbol.getName()))
                .toList()
                .forEach(sym -> symbols.remove(sym));
    }
}
