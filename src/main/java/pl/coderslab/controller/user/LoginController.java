package pl.coderslab.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.entity.User;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String getLogin(Model model){
        model.addAttribute("user", new User());
        System.out.println("pizda");
        return "user/user-login";
    }
}
