package pl.coderslab.entity.orders;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "symbols")
@Data
public class Symbol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Pattern(regexp = ".*(usdt|USDT)$", message = "Nazwa powinna zawierać 'usdt'")
    @Column(name = "symbol")
    private String name;

}
