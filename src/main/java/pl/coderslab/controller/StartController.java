package pl.coderslab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StartController {


    @GetMapping("/")
    public String startView(){
        return "unlogged/start";
    }

}
