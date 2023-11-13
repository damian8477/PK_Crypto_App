package pl.coderslab.service.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.interfaces.SourceService;
import pl.coderslab.repository.SourceRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {
    private final SourceRepository sourceRepository;

    @Override
    @Transactional
    public Source findByName(String name) {
        return sourceRepository.findByName(name);
    }
}
