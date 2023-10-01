package pl.coderslab.entity.strategy;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "indicator")
@Data
public class Indicator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

}
