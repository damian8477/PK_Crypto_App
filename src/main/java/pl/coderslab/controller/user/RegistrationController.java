package pl.coderslab.controller.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.entity.user.User;
import pl.coderslab.repository.UserRepository;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String preparepRegistrationPage() {
        return "registration-page";
    }

    @PostMapping
    public String processRegistrationPage(String username,
                                          String password,
                                          String firstName,
                                          String lastName) {
        System.out.println("password:" + password);
        User user = new User();
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(true);
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return "redirect:/login";
    }

}

