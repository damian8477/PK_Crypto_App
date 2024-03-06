package pl.coderslab.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.coderslab.strategy.service.Strategy111Service;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    @GetMapping
    public String getTest(){
        logger.info("Test work");
        return "Test work";
    }
}
