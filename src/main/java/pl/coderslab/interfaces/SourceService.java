package pl.coderslab.interfaces;

import pl.coderslab.entity.strategy.Source;

import java.util.List;

public interface SourceService {
    Source findByName(String name);

    Source findByNameWithSymbols(String name);

    Source findById(int id);

    List<Source> findAll();

    Source save(Source source);

    void deleteById(int id);
}
