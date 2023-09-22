package pl.coderslab.service.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {
    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void print(){
        //todo
    }
}
