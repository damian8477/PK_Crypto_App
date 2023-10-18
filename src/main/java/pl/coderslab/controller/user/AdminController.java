package pl.coderslab.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.entity.user.User;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.repository.UserRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/user-list")
    public String getUserList(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/userslist";
    }

    @GetMapping("/edit")
    public String getEditView(Model model, @RequestParam Long userId) {
        model.addAttribute("user", userRepository.findById(userId));
        return "admin/edit";
    }

    @PostMapping("/edit")
    public String postEditView(User user) {
        userRepository.save(user);
        return "redirect:/admin/user-list";
    }

    @GetMapping("/delete")
    public String getDeleteView(Model model, @RequestParam Long userId) {
        model.addAttribute("user", userService.getUserWithUserSettings(userId));
        return "admin/delete";
    }

    @PostMapping("/delete")
    public String postEditView(@RequestParam Long userId) {
        userRepository.deleteById(userId);
        return "redirect:/admin/user-list";
    }

    @PostMapping("/deletet")
    public String postEditViedw(@RequestParam Long userId) {
        userService.deleteById(userId);
        return "redirect:/admin/user-list";
    }
}
