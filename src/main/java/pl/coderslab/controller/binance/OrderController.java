package pl.coderslab.controller.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.entity.orders.Order;
import pl.coderslab.entity.user.User;
import pl.coderslab.repository.OrderRepository;
import pl.coderslab.service.binance.OrderService;
import pl.coderslab.service.binance.orders.CloseService;
import pl.coderslab.service.entity.UserService;

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

    @GetMapping("/list")
    public String getOrderList(Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        List<Order> orderList = orderService.prepareOrderList(user, user.getOrders());
        model.addAttribute("orders", orderList);
        return "/app/binance/orders/order-list";
    }

    @GetMapping("/close-order") //todo zrobic najpierw przekazanie widoku potwierdzajace, ale moze w sumie wyslac tez ile jest na koncie i pobrac ile z tego zamknac sprawdzajac czy jest to te 5$
    public String getCloseOrder(Model model,
                                @RequestParam(required = false) Long orderId,
                                @RequestParam(required = false) String symbol,
                                @AuthenticationPrincipal UserDetails authenticatedUser){
        User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
        if(!isNull(orderId)){
            Order order = orderRepository.findByUserId(user.getId()).stream()
                    .filter(s->s.getId() == orderId).findFirst().orElse(null);
            if(!isNull(order)){
                closeService.closeOrderByUser(order, user);//todo
            }
        } else if(!isNull(symbol)) {

        }
        return "redirect:/app/orders/list";
    }
    //todo
    //dla administratora podglad na zlecenia uzytkownikow, przejscie z ekranu uzytkownikow, mozliwosc zamykania zlecenia KILL
    //edycja, zamykanie, dokładanie, hedge, zmiana tp, zmiana sl, update tp i sl to aktualnego stanu zlecenia,
}
