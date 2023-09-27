package pl.coderslab.entity.orders;


import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "symbols")
@Data
public class Symbol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String symbol;
}
