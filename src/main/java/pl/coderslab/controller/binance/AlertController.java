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
import pl.coderslab.interfaces.AlertService;
import pl.coderslab.interfaces.SymbolService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.model.AlertSetting;
import pl.coderslab.repository.AlertRepository;

import javax.validation.Valid;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
/**
 * Controller class handling alert-related operations in the Binance application.
 */
@Controller
@RequestMapping("/app/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final SymbolService symbolService;
    private final UserService userService;
    private final AlertService alertService;
    private final AlertRepository alertRepository;
    /**
     * Displays the list of alerts for the current user.
     *
     * @param model             the model to be used in the view
     * @param authenticatedUser the currently authenticated user
     * @return the view name
     */
    @GetMapping("/list")
    public String getAlertListView(Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        List<Map<String, List<Alert>>> alertList = alertService.getAlertList(user.getAlerts());
        model.addAttribute("alerts", alertList);
        return "/app/binance/alert/alert-list";
    }
    /**
     * Displays the form for adding a new alert for a specific symbol.
     *
     * @param symbolId the ID of the symbol
     * @param model    the model to be used in the view
     * @return the view name
     */
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
    /**
     * Handles the form submission for adding a new alert.
     *
     * @param alert              the alert data from the form
     * @param bindingResult      the result of the form validation
     * @param model              the model to be used in the view
     * @param authenticatedUser the currently authenticated user
     * @return the view name or a redirect URL
     */
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
        return "redirect:/app/binance/symbol-list";
    }
    /**
     * Displays the view for deleting a specific alert.
     *
     * @param alertId the ID of the alert to be deleted
     * @param model   the model to be used in the view
     * @return the view name
     */
    @GetMapping("/delete")
    public String getDeleteView(@RequestParam Long alertId, Model model) {
        Alert alert = alertRepository.findById(alertId).get();
        if (isNull(alert)) {
            return "redirect:/app/alerts/list";
        }
        model.addAttribute("alert", alert);
        return "/app/binance/alert/delete";
    }
    /**
     * Handles the form submission for deleting a specific alert.
     *
     * @param alertId            the ID of the alert to be deleted
     * @return a redirect URL
     */
    @PostMapping("/delete")
    public String deleteAlert(@RequestParam Long alertId) {
        alertRepository.deleteById(alertId);
        return "redirect:/app/alerts/list";
    }
    /**
     * Provides the position sides for the alert form.
     *
     * @return an array of position sides
     */
    @ModelAttribute("positionSides")
    public PositionSide[] positionSides() {
        PositionSide[] positionSides = PositionSide.values();
        positionSides = Arrays.stream(positionSides).filter(s -> !s.equals(PositionSide.BOTH)).toArray(PositionSide[]::new);
        return positionSides;
    }


}
