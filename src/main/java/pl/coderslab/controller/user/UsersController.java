package pl.coderslab.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.entity.user.User;
import pl.coderslab.entity.user.UserSetting;
import pl.coderslab.enums.TokenType;
import pl.coderslab.interfaces.TelegramCodeService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.interfaces.UserSettingService;
import pl.coderslab.interfaces.UserTokenService;
import pl.coderslab.model.NewPassword;
import pl.coderslab.repository.UserRepository;
import pl.coderslab.repository.UserSettingRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.DayOfWeek;

@Controller
@RequestMapping("/app/user")
@RequiredArgsConstructor
public class UsersController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserSettingRepository userSettingRepository;
    private final UserSettingService userSettingService;
    private final TelegramCodeService telegramCodeService;
    private final UserTokenService userTokenService;
    private final PasswordEncoder passwordEncoder;

    private final String REDIRECT = "redirect:/app/user/settings";

    @GetMapping("/change-pass")
    public String getChangePasswordView(Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        User user = userService.getUserBasic(authenticatedUser.getUsername());
        if (userTokenService.generateUserToken(user, TokenType.PASSWORD)) {
            model.addAttribute("newPassword", new NewPassword());
            return "/app/settings/change-password";
        }
        //todo dorobic jakies info ze nie ma maila czy cos
        return REDIRECT;
    }

    @PostMapping("/change-pass")
    public String changePassword(@Valid NewPassword newPassword, BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (bindingResult.hasErrors() || !newPassword.getPass1().equals(newPassword.getPass2())) {
            if (!newPassword.getPass1().equals(newPassword.getPass2())) {
                bindingResult.rejectValue("pass2", "Hasła nie pasują do siebie", "Hasła nie pasują do siebie");
            }
            model.addAttribute("newPassword", newPassword);
            return "/app/settings/change-password";
        }
        User user = userService.getUserBasic(authenticatedUser.getUsername());
        if (userTokenService.checkUserToken(user, newPassword.getToken())) {
            String encodedPassword = passwordEncoder.encode(newPassword.getPass1());
            user.setPassword(encodedPassword);
            userRepository.save(user);
        }
        return REDIRECT;
    }

    @GetMapping("/edit")
    public String getUserEditView(Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        User user = userService.getUserBasic(authenticatedUser.getUsername());
        model.addAttribute("user", user);
        return "/app/settings/edit-user";
    }

    @PostMapping("/edit")
    public String getUserEditView(@Valid User user, BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails authenticatedUser, HttpServletRequest request, HttpServletResponse response) {
        User userDb = userService.getUserBasic(authenticatedUser.getUsername());
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "/app/settings/edit-user";
        }
        userRepository.save(user);
        if (userDb.getUsername().equals(user.getUsername())) {
            return REDIRECT;
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            return "redirect:/login";
        }
    }


    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        userService.deleteById(id);
        return "redirect:/admin/user-list";
    }

    @GetMapping("/settings")
    public String getSettings(Model model) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserWithUserSettingsByUserName(userName);
        model.addAttribute("user", user);
        return "/app/settings/user-setting";
    }

    @PostMapping("/add-hours")
    public String addHour(@RequestParam Integer hour, Model model) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserWithUserSettingsByUserName(userName);
        model.addAttribute("user", user);
        return "/app/settings/user-setting";
    }

    @GetMapping("/add-user-setting")
    public String getAddUserSettingView(Model model) {
        model.addAttribute("userSetting", new UserSetting());
        return "/app/settings/add-user-setting";
    }

    @PostMapping("/add-user-setting")
    public String addUserSettingView(@Valid UserSetting userSetting, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userSetting", userSetting);
            return "/app/settings/add-user-setting";
        }
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        userSetting.setUser(userService.getUserWithUserSettingsByUserName(userName));
        userSettingRepository.save(userSetting);
        return "redirect:/app/user/settings";
    }


    @GetMapping("/edit-user-setting")
    public String getEditUserSettingView(@RequestParam Integer settingId, Model model) {
        UserSetting userSetting = userSettingService.getUserSetting(settingId);
        model.addAttribute("userSetting", userSetting);
        model.addAttribute("generatedCode", telegramCodeService.getCode(6, userSetting.getUser()));
        return "/app/settings/edit-user-setting";
    }

    @PostMapping("/edit-user-setting")
    public String editUserSettingView(@Valid UserSetting userSetting, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userSetting", userSetting);
            return "/app/settings/edit-user-setting";
        }
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        userSetting.setUser(userService.getUserWithUserSettingsByUserName(userName));
        userSettingRepository.save(userSetting);
        return REDIRECT;
    }


    @GetMapping("/delete-user-setting")
    public String getDeleteUserSetting(@RequestParam int settingId, Model model) {
        model.addAttribute("settingId", settingId);
        return "/app/settings/delete-user-setting";
    }

    @PostMapping("/delete-user-setting")
    public String deleteUserSetting(@RequestParam int settingId) {
        userSettingRepository.deleteById(settingId);
        return REDIRECT;
    }

    @ModelAttribute("day")
    public DayOfWeek[] dayOfWeeks() {
        return new DayOfWeek[]{
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY
        };
    }


}
