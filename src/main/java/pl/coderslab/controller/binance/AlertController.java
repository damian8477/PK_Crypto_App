package pl.coderslab.controller.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.entity.alert.Alert;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.entity.user.User;
import pl.coderslab.model.AlertSetting;
import pl.coderslab.repository.AlertRepository;
import pl.coderslab.repository.SymbolRepository;
import pl.coderslab.service.entity.SymbolService;
import pl.coderslab.service.entity.UserService;
import pl.coderslab.service.telegram.AlertService;

import javax.validation.Valid;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

@Controller
@RequestMapping("/app/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final SymbolService symbolService;
    private final UserService userService;
    private final AlertService alertService;
    private final AlertRepository alertRepository;

    @GetMapping("/list")
    public String getAlertListView(Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        List<Map<String, List<Alert>>> alertList = alertService.getAlertList(user.getAlerts());
        model.addAttribute("alerts", alertList);
        for (Map<String, List<Alert>> symbolAlerts : alertList) {
            for (Map.Entry<String, List<Alert>> entry : symbolAlerts.entrySet()) {
                String symbol = entry.getKey();
                List<Alert> alerts = entry.getValue();
                System.out.println("Symbol: " + symbol);
                for (Alert alert : alerts) {
                    System.out.println(" - Cena: " + alert.getPrice());
                    // Dodatkowe informacje...
                }
            }
        }
        return "/app/binance/alert/alert-list";
    }

    @GetMapping("/alert")
    public String getAlertView(@RequestParam int symbolId, Model model) {
        Symbol symbol = symbolService.findById(symbolId);
        if (!isNull(symbol)) {
            model.addAttribute("alert", symbolService.getBasicAlert(symbol.getName()));
            return "/app/binance/alert/add-alert";
        }
        //todo dodac i wyswietlic info na widoku
        return "redirect:/app/binance/symbol-list";
    }

    @PostMapping("/alert")
    public String addAlert(@Valid AlertSetting alert, BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("alert", alert);
            return "/app/binance/alert/add-alert";
        } else {
            if (!isNull(alert)) {
                User user = userService.getUserBasic(authenticatedUser.getUsername());
                alertService.addAlert(user, alert);
                return "redirect:/app/alerts/list";
            }
        }
        //todo dodac i wyswietlic info na widoku
        return "redirect:/app/binance/symbol-list";
    }

    @GetMapping("/delete")
    public String getDeleteView(@RequestParam Long alertId, Model model) {
        Alert alert = alertRepository.findById(alertId).get();
        if (isNull(alert)) {
            return "redirect:/app/alerts/list";
        }
        model.addAttribute("alert", alert);
        return "/app/binance/alert/delete";
    }

    @PostMapping("/delete")
    public String deleteAlert(@RequestParam Long alertId, @AuthenticationPrincipal UserDetails authenticatedUser) {
        alertRepository.deleteById(alertId);
        return "redirect:/app/alerts/list";
    }

    @ModelAttribute("positionSides")
    public PositionSide[] positionSides() {
        PositionSide[] positionSides = PositionSide.values();
        positionSides = Arrays.stream(positionSides).filter(s -> !s.equals(PositionSide.BOTH)).toArray(PositionSide[]::new);
        return positionSides;
    }


}
