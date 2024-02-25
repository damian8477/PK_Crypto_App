package pl.coderslab.entity.orders;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "symbols")
@Getter
@Setter
public class Symbol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Pattern(regexp = ".*(usdt|USDT)$", message = "Nazwa powinna zawierać 'usdt'")
    @Column(name = "symbol")
    private String name;
    private boolean active = true;

}
