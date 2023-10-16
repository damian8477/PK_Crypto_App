package pl.coderslab.interfaces;

import org.springframework.scheduling.annotation.Scheduled;

public interface ScheduledService {
    @Scheduled(fixedDelay = 60000, initialDelay = 1000)
    void check();
}
