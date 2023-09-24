package pl.coderslab.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.repository.UserRepository;

@Controller
@RequestMapping("/app/user")
@RequiredArgsConstructor
public class UsersController {
    private final UserRepository userRepository;

    @PostMapping("/delete")
    public String getAdd(@RequestParam Long id){
        userRepository.deleteById(id);
        return "redirect:/admin/user-list";
    }
}
