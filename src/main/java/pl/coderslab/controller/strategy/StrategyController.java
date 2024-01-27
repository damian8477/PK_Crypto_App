package pl.coderslab.controller.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.MarginType;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.repository.SourceRepository;
import pl.coderslab.repository.StrategyRepository;

import javax.validation.Valid;

@Controller
@RequestMapping("/app/strategy")
@RequiredArgsConstructor
public class StrategyController {
    private final UserService userService;
    private final StrategyRepository strategyRepository;
    private final SourceRepository sourceRepository;

    private static final String REDIRECT = "redirect:/app/strategy/list";

    @GetMapping("/list")
    public String getMyString(Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        model.addAttribute("strategies", user.getStrategies());
        return "/app/strategy/list";
    }

    @GetMapping("/edit")
    public String getEditMyListView(@RequestParam int strategyId, Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        model.addAttribute("strategy", user.getStrategies().stream().filter(s -> s.getId() == strategyId).findFirst().orElse(null));
        return "/app/strategy/edit";
    }

    @PostMapping("/edit")
    public String getEditMyListView(@Valid @ModelAttribute("strategy") Strategy strategy, BindingResult bindingResult, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (bindingResult.hasErrors()) {
            return "app/strategy/edit";
        }
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        strategy.setUser(user);
        strategyRepository.save(strategy);
        return REDIRECT;
    }

    @GetMapping("/add")
    public String getAddStrategy(Model model) {
        model.addAttribute("strategy", new Strategy());
        return "/app/strategy/add";
    }

    @PostMapping("/add")
    public String addStrategy(@Valid @ModelAttribute("strategy") Strategy strategy, BindingResult bindingResult, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (bindingResult.hasErrors()) {
            return "app/strategy/add";
        }
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        strategy.setUser(user);
        strategyRepository.save(strategy);
        return REDIRECT;
    }

    @GetMapping("/delete")
    public String getDeleteUserStrategyView(@RequestParam long strategyId, Model model) {
        model.addAttribute("strategyId", strategyId);
        return "/app/strategy/delete";
    }

    @PostMapping("/delete")
    public String deleteUserStrategy(@RequestParam long strategyId) {
        strategyRepository.deleteById(strategyId);
        return REDIRECT;
    }


    @ModelAttribute("marginTypes")
    public MarginType[] marginTypes() {
        return MarginType.values();
    }

    @ModelAttribute("sourceList")
    public Source[] sourceList() {
        return sourceRepository.findAll().toArray(new Source[0]);
    }
}
