package pl.coderslab.controller.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.entity.orders.HistoryOrder;
import pl.coderslab.entity.user.User;
import pl.coderslab.repository.HistoryOrderRepository;
import pl.coderslab.service.entity.UserService;

import java.util.List;

@Controller
@RequestMapping("/app/history-orders")
@RequiredArgsConstructor
public class HistoryController {
    private final UserService userService;
    private final HistoryOrderRepository historyOrderRepository;

    @GetMapping("/list")
    public String getOrderList(Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        List<HistoryOrder> historyOrders = historyOrderRepository.findAllByUserId(user.getId()).stream()
                .sorted((o2, o1) -> o1.getCreated().compareTo(o2.getCreated())).toList();
        model.addAttribute("historyOrders", historyOrders);
        return "/app/binance/history/list";
    }

}
