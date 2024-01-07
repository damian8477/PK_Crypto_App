package pl.coderslab.controller.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.user.User;
import pl.coderslab.interfaces.BinanceBasicService;
import pl.coderslab.interfaces.CloseService;
import pl.coderslab.interfaces.OrderService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.repository.OrderRepository;

import java.util.List;

import static java.util.Objects.isNull;


@Controller
@RequestMapping("/app/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserService userService;
    private final CloseService closeService;
    private final BinanceBasicService binanceUserService;

    @GetMapping("/list")
    public String getOrderList(Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        List<Order> orderList = orderService.prepareOrderList(user, user.getOrders());
        model.addAttribute("orders", orderList);
        return "/app/binance/orders/list";
    }

    @GetMapping("/close-order")
    //todo zrobic najpierw przekazanie widoku potwierdzajace, ale moze w sumie wyslac tez ile jest na koncie i pobrac ile z tego zamknac sprawdzajac czy jest to te 5$
    public String getCloseOrder(Model model,
                                @RequestParam(required = false) Long orderId,
                                @RequestParam(required = false) String symbol,
                                @AuthenticationPrincipal UserDetails authenticatedUser) {
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        if (!isNull(orderId)) {
            Order order = orderRepository.findByUserId(user.getId()).stream()
                    .filter(s -> s.getId() == orderId).findFirst().orElse(null);
            if (!isNull(order)) {
                closeService.closeOrderByUser(order, user, null);//todo
            }
        } else if (!isNull(symbol)) {
            Order order = orderService.getOrderBySymbol(user, symbol);
            model.addAttribute("order", order);
            model.addAttribute("marketPrice", binanceUserService.getMarketPriceString(user, symbol));
            return "/app/binance/orders/close-symbol";
        }
        return "redirect:/app/orders/list";
    }

    @PostMapping("/close-order")
    public String closeSymbol(Order order, @AuthenticationPrincipal UserDetails authenticatedUser) {
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        //todo sprawdzenie czy lot jest prawidłowy powyzej 5$ i w rozdzielczosci coina
        closeService.closeOrderByUser(order, user, order.getLot());
        return "redirect:/app/orders/list";
    }
    //todo
    //dla administratora podglad na zlecenia uzytkownikow, przejscie z ekranu uzytkownikow, mozliwosc zamykania zlecenia KILL
    //edycja, zamykanie, dokładanie, hedge, zmiana tp, zmiana sl, update tp i sl to aktualnego stanu zlecenia,
}
