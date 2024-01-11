package pl.coderslab.controller.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserToken;
import pl.coderslab.interfaces.EmailService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.interfaces.UserTokenService;
import pl.coderslab.model.NewPassword;
import pl.coderslab.repository.UserRepository;
import pl.coderslab.service.email.RemindPasswordServiceImpl;

import javax.validation.Valid;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final RemindPasswordServiceImpl remindPasswordService;
    private final PasswordEncoder passwordEncoder;
    private final UserTokenService userTokenService;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

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

    @GetMapping("/remind-password")
    public String remindPassword() {
        return "anonymous/remind-password";
    }

    @PostMapping("/remind-password")
    public String remindPassword(String email, Locale locale) {
        remindPasswordService.remindPassword(email, locale);
        return "anonymous/remind-password-confirm";
    }

    @GetMapping("/remind-pass")
    public String remindPass(@RequestParam String token, Model model) {
        if (userTokenService.existsByToken(token)) {
            System.out.println(token);
            model.addAttribute("newPassword", new NewPassword(token));
            NewPassword newPassword = new NewPassword(token);
            System.out.println(newPassword);
            return "anonymous/reset-password";
        }
        return "redirect:/login";
    }

    @PostMapping("/remind-pass")
    public String processRegistrationPage2(@Valid NewPassword newPassword, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors() || !newPassword.getPass1().equals(newPassword.getPass2())) {
            if (!newPassword.getPass1().equals(newPassword.getPass2())) {
                bindingResult.rejectValue("pass2", "Hasła nie pasują do siebie", "Hasła nie pasują do siebie");
            }
            model.addAttribute("newPassword", newPassword);
            return "/app/settings/change-password";
        }
        System.out.println(newPassword.getToken());
        String encodedPassword = passwordEncoder.encode(newPassword.getPass1());
        UserToken userToken = userTokenService.getUserToken(newPassword.getToken());
        User user = userToken.getUser();
        user.setPassword(encodedPassword);
        userRepository.save(user);

        logger.info("Password has changed!");
        logger.info(user.getUsername() + " " + user.getFirstName() + " " + user.getLastName() + " " + user.getEmail());

        return "redirect:/login";
    }
}
