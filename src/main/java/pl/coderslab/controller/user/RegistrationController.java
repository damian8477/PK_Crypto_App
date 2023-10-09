package pl.coderslab.controller.user;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.entity.user.User;
import pl.coderslab.repository.UserRepository;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String preparepRegistrationPage() {
        return "anonymous/registration-page";
    }

    @PostMapping
    public String processRegistrationPage(String username,
                                          String password,
                                          String firstName,
                                          String lastName) {
        User user = new User();
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(true);
        user.setRole("ROLE_USER");
        userRepository.save(user);

        logger.info("New user register!");
        logger.info(username + " " + firstName + " " + lastName);

        return "redirect:/login";
    }

}

