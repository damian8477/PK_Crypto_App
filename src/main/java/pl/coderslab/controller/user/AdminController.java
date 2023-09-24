package pl.coderslab.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.entity.user.User;
import pl.coderslab.repository.UserRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    @GetMapping("/user-list")
    public String getUserList(Model model){
        model.addAttribute("users", userRepository.findAll());

        System.out.println("kurwa");
        return "admin/userslist";
    }

    @GetMapping("/edit")
    public String getEditView(Model model, @RequestParam Long userId){
        model.addAttribute("user", userRepository.findById(userId));
        return "admin/edit";
    }

    @PostMapping("/edit")
    public String postEditView(User user){
        userRepository.save(user);
        return "admin/userslist";
    }

    @GetMapping("/delete")
    public String getDeleteView(Model model, @RequestParam Long userId){
        model.addAttribute("user", userRepository.findById(userId));
        return "admin/delete";
    }

    @PostMapping("/delete")
    public String postEditView(@RequestParam Long userId){
        userRepository.deleteById(userId);
        return "admin/userslist";
    }
}
