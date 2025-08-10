package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.service.SimilarityProcessor;
import ru.practicum.service.UserActionProcessor;

@Component
@RequiredArgsConstructor
public class AnalyzerStarter implements CommandLineRunner {
    private final UserActionProcessor userActionService;
    private final SimilarityProcessor eventSimilarityService;

    Logger log = org.slf4j.LoggerFactory.getLogger(AnalyzerStarter.class);
    @Override
    public void run(String... args) {
        Thread userActionThread = new Thread(userActionService);
        userActionThread.setName("userActionHandlerThread");
        userActionThread.start();

        log.info("Запуск userActionHandlerThread");
        eventSimilarityService.start();
    }
}