package pl.coderslab.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.repository.UserRepository;
import pl.coderslab.repository.UserSettingRepository;
import pl.coderslab.service.entity.UserService;
import pl.coderslab.service.entity.UserSettingService;

import javax.validation.Valid;

@Controller
@RequestMapping("/app/user")
@RequiredArgsConstructor
public class UsersController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserSettingRepository userSettingRepository;
    private final UserSettingService userSettingService;

    @PostMapping("/delete")
    public String getAdd(@RequestParam Long id){
        userRepository.deleteById(id);
        return "redirect:/admin/user-list";
    }

    @GetMapping("/settings")
    public String getSettings(Model model){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("user", userService.getUserWithUserSettingsByUserName(userName));
        return "/app/settings/user-setting";
    }

    @GetMapping("/add-user-setting")
    public String getAddUserSettingView(Model model){
        model.addAttribute("userSetting", new UserSetting());
        return "/app/settings/add-user-setting";
    }

    @PostMapping("/add-user-setting")
    public String addUserSettingView(@Valid UserSetting userSetting, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("userSetting", userSetting);
            return "/app/settings/add-user-setting";
        }
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        userSetting.setUser(userService.getUserWithUserSettingsByUserName(userName));
        userSettingRepository.save(userSetting);
        return "redirect:/app/user/settings";
    }

    @GetMapping("/edit-user-setting")
    public String getEditUserSettingView(@RequestParam Integer settingId,  Model model){
        model.addAttribute("userSetting", userSettingService.getUserSetting(settingId));
        return "/app/settings/edit-user-setting";
    }
    @PostMapping("/edit-user-setting")
    public String editUserSettingView(@Valid UserSetting userSetting, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("userSetting", userSetting);
            return "/app/settings/edit-user-setting";
        }
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        userSetting.setUser(userService.getUserWithUserSettingsByUserName(userName));
        userSettingRepository.save(userSetting);
        return "redirect:/app/user/settings";
    }


    @GetMapping("/delete-user-setting")
    public String getDeleteUserSetting(@RequestParam int settingId, Model model){
        model.addAttribute("settingId", settingId);
        return "/app/settings/delete-user-setting";
    }

    @PostMapping("/delete-user-setting")
    public String deleteUserSetting(@RequestParam int settingId){
        userSettingRepository.deleteById(settingId);
        return "redirect:/app/user/settings";
    }


}
