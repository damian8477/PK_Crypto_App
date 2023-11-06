package pl.coderslab.interfaces;

import pl.coderslab.entity.strategy.Source;

public interface SourceService {
    Source findByName(String name);
}
