package pl.coderslab.entity.source;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "sources")
@Getter
@Setter
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean active;
    private String type;
    private boolean local;
    private boolean sliding;
    private double factor;
    private int maxLeverage;
    private boolean hedge;
}
