package pl.coderslab.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.coderslab.service.telegram.TelegramInfoServiceImpl;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final TelegramInfoServiceImpl telegramInfoService;
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping
    public String getTest() {
        logger.info("Test work");
        telegramInfoService.sendMessage(null, "pzida");
        return "Test work";
    }
}
