package pl.coderslab.service.entity;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.interfaces.SourceService;
import pl.coderslab.repository.SourceRepository;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {
    private final SourceRepository sourceRepository;

    @Override
    public Source findByName(String name) {
        return sourceRepository.findByName(name);
    }

    @Override
    @Transactional
    public Source findByNameWithSymbols(String name) {
        Source source = sourceRepository.findByName(name);
        if(!isNull(source)){
            Hibernate.initialize(source.getSymbols());
        }
        return source;
    }

    @Override
    @Transactional
    public Source findById(int id){
        Source source = sourceRepository.findById(id);
        if(!isNull(source)){
            Hibernate.initialize(source.getSymbols());
        }
        return source;
    }

    @Override
    public List<Source> findAll() {
        return sourceRepository.findAll();
    }

    @Override
    public Source save(Source source){
        return sourceRepository.save(source);
    }

    @Override
    public void deleteById(int id){
        sourceRepository.deleteById(id);
    }

}
