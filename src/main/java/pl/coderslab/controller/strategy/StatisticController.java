package pl.coderslab.controller.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.entity.orders.HistoryOrder;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.interfaces.SourceService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.model.statistic.SourceStat;
import pl.coderslab.repository.HistoryOrderRepository;
import pl.coderslab.service.statistic.StatisticService;

import java.util.List;

@Controller
@RequestMapping("/app/statistic")
@RequiredArgsConstructor
public class StatisticController {
    public final HistoryOrderRepository historyOrderRepository;
    public final StatisticService statisticService;
    public final SourceService sourceService;
    private final UserService userService;

    @GetMapping("/data")
    public String getData() {
        List<HistoryOrder> historyOrderList = historyOrderRepository.findAllBySourceAndUserId(3, 1000L);
        return "/app/statistic/data";
    }

    @GetMapping("/menu")
    public String getMenuView() {
        return "/app/statistic/menu";
    }

    @GetMapping("/source")
    public String getSource(@RequestParam int sourceId, boolean userBot, Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        long userId = 1000L;
        if(!userBot) {
            userId = userService.getUserBasic(authenticatedUser.getUsername()).getId();
        }
        List<HistoryOrder> historyOrderList = historyOrderRepository.findAllBySourceAndUserId(sourceId, userId);
        Source source = sourceService.findById(sourceId);
        SourceStat sourceStat = statisticService.getSourceStatistic(historyOrderList);
        model.addAttribute("sourceStat", sourceStat);
        model.addAttribute("source", source);
        return "/app/statistic/source";
    }

    @ModelAttribute("sourceList")
    public Source[] sourceList() {
        return sourceService.findAll().toArray(new Source[0]);
    }


}
