package pl.coderslab.controller.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.entity.strategy.StrategySetting;
import pl.coderslab.repository.StrategySettingRepository;

import javax.validation.Valid;

@Controller
@RequestMapping("/app/strategy")
@RequiredArgsConstructor
public class StrategyController {
    private final StrategySettingRepository strategySettingRepository;

    @GetMapping("/list")
    public String getStrategyList(Model model) {
        model.addAttribute("strategies", strategySettingRepository.findAll());
        return "/app/strategy/list";
    }

    @GetMapping("/add")
    public String getAddStrategy(Model model) {
        model.addAttribute("strategy", new StrategySetting());
        return "/app/strategy/add";
    }

    @PostMapping("/add")
    public String addStrategy(@Valid @ModelAttribute("strategy") StrategySetting strategySetting, BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (bindingResult.hasErrors()) {
            return "/app/strategy/add";
        }
        if (authenticatedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            strategySetting.setAdminStrategy(true);
        }
        strategySettingRepository.save(strategySetting);
        return "redirect:/app/strategy/list";
    }

    @GetMapping("/delete")
    public String getDeleteStrategy(@RequestParam int strategyId, Model model) {
        model.addAttribute("strategyId", strategyId);
        return "/app/strategy/delete";
    }

    @PostMapping("/delete")
    public String deleteStrategy(@RequestParam int strategyId) {
        strategySettingRepository.deleteById(strategyId);
        return "redirect:/app/strategy/list";
    }
}
