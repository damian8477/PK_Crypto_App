package pl.coderslab.controller.user;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.repository.UserRepository;
import pl.coderslab.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
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
        return "redirect:/admin/user-list";
    }

    @GetMapping("/delete")
    public String getDeleteView(Model model, @RequestParam Long userId){
        model.addAttribute("user", userService.getUserWithUserSettings(userId));
        return "admin/delete";
    }

    @PostMapping("/delete")
    public String postEditView(@RequestParam Long userId){
        System.out.println("chujekKKKKKKKKKKKKKKKKKKkkkk");
        userRepository.deleteById(userId);
        return  "redirect:/admin/user-list";
    }

    @PostMapping("/deletet")
    public String postEditViedw(@RequestParam Long userId){
        System.out.println("chujekKKKKKKKKKKKKKKKKKKkkkk");
        userRepository.deleteById(userId);
        return  "redirect:/admin/user-list";
    }
    @PostMapping("/pizda")
    @ResponseBody
    public String pizda(@RequestParam Long userId){
        System.out.println("chujekKKKKKKKKKKKKKKKKKKkkkk");
        return  "redirect:/admin/user-list";
    }
}
