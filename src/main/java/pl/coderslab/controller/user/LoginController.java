package pl.coderslab.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.entity.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.http.HttpRequest;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String prepareLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "unlogged/login";
    }

    @GetMapping("/starter")
    public String startPage(){
        System.out.println("starter");
        return "/app/start";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse resp){
        System.out.println("logout");
        Cookie cookie = new Cookie("JSESSIONID", "/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        return "/unlogged/login";
    }

}
