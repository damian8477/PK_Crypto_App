package pl.coderslab.controller.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.coderslab.entity.user.User;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String prepareLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "anonymous/login";
    }

    @GetMapping("/starter")
    public String startPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        model.addAttribute("username", userName);
        return "/app/start";
    }

}
