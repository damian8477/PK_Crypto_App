package pl.coderslab.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class Testy {
    @GetMapping("/1")
    public String test1(){
        System.out.println("clf59olo5MjQeQ8Qxe6RnPC4ePjxZsAeTdxZgdWedBtWt1f4Z7zohD2qEIYOgXdk".length());
        System.out.println("NWtuZVbCa8esr4VG6usHzS0Ms1aMz7NwLBjPcUmU0SGRb6uXKvpiK77HM1ntHoMx".length());
        return "testsec";
    }
}
