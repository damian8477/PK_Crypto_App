package pl.coderslab.controller.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.entity.strategy.Strategy;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.MarginType;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.repository.SourceRepository;
import pl.coderslab.repository.StrategyRepository;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/app/strategy")
@RequiredArgsConstructor
public class StrategyController {
    private final SourceRepository sourceRepository;
    private final UserService userService;
    private final StrategyRepository strategyRepository;
    @GetMapping("/list")
    public String getStrategyList(Model model) {
        model.addAttribute("strategies", sourceRepository.findAll());
        return "/app/strategy/list";
    }

    @GetMapping("/add")
    public String getAddStrategy(Model model) {
        model.addAttribute("strategy", new Source());
        return "/app/strategy/add";
    }

    @PostMapping("/add")
    public String addStrategy(@Valid @ModelAttribute("strategy") Source strategySetting, BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (bindingResult.hasErrors()) {
            return "/app/strategy/add";
        }
        if (authenticatedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            strategySetting.setAdminStrategy(true);
        }
        sourceRepository.save(strategySetting);
        return "redirect:/app/strategy/list";
    }

    @GetMapping("/delete")
    public String getDeleteView(@RequestParam int strategyId, Model model) {
        model.addAttribute("strategyId", strategyId);
        return "/app/strategy/delete";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam int strategyId) {
        sourceRepository.deleteById(strategyId);
        return "redirect:/app/strategy/list";
    }

    @GetMapping("/edit")
    public String getEditView(@RequestParam int strategyId, Model model){
        model.addAttribute("strategy", sourceRepository.findById(strategyId));
        return "/app/strategy/edit";
    }

    @PostMapping("/edit")
    public String editStrategy(@Valid @ModelAttribute("strategy") Source strategySetting, BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (bindingResult.hasErrors()) {
            return "/app/strategy/edit";
        }
        if (authenticatedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            strategySetting.setAdminStrategy(true);
        }
        sourceRepository.save(strategySetting);
        return "redirect:/app/strategy/list";
    }

    @GetMapping("/my-list")
    public String getMyString(Model model, @AuthenticationPrincipal UserDetails authenticatedUser){
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        model.addAttribute("strategies", user.getStrategies());
        return "/app/strategy/my-list";
    }

    @GetMapping("/my-list-edit")
    public String getEditMyListView(@RequestParam int strategyId, Model model, @AuthenticationPrincipal UserDetails authenticatedUser){
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        model.addAttribute("strategy", user.getStrategies().stream().filter(s->s.getId()==strategyId).findFirst().orElse(null));
        return "/app/strategy/edit-my-list";
    }
    @PostMapping("/my-list-edit")
    public String getEditMyListView(@Valid @ModelAttribute("strategy") Strategy strategy, BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails authenticatedUser){
        if (bindingResult.hasErrors()) {
            return "/app/strategy/edit-my-list";
        }
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        strategy.setUsers(List.of(user));
        strategyRepository.save(strategy);
        return "redirect:/app/strategy/my-list";
    }

    @ModelAttribute("marginTypes")
    public MarginType[] marginTypes() {
        return MarginType.values();
    }
}
