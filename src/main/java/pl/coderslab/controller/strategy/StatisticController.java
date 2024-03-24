package pl.coderslab.controller.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.entity.orders.HistoryOrder;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.interfaces.SourceService;
import pl.coderslab.interfaces.UserService;
import pl.coderslab.model.statistic.SourceStat;
import pl.coderslab.repository.HistoryOrderRepository;
import pl.coderslab.service.statistic.StatisticService;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/app/statistic")
@RequiredArgsConstructor
public class StatisticController {
    public final HistoryOrderRepository historyOrderRepository;
    public final StatisticService statisticService;
    public final SourceService sourceService;
    private final UserService userService;

    @GetMapping("/menu")
    public String getMenuView() {
        return "/app/statistic/menu";
    }

    @GetMapping("/source")
    public String getSource(@RequestParam int sourceId, boolean userBot, boolean openTime, Model model, @AuthenticationPrincipal UserDetails authenticatedUser, HttpSession session) {
        long userId = 1000L;
        if (!userBot) {
            userId = userService.getUserBasic(authenticatedUser.getUsername()).getId();
        }
        List<HistoryOrder> historyOrderList = historyOrderRepository.findAllBySourceAndUserId(sourceId, userId);
        Source source = sourceService.findById(sourceId);
        SourceStat sourceStat = statisticService.getSourceStatistic(historyOrderList, source, openTime);
        model.addAttribute("sourceStat", sourceStat);
        model.addAttribute("source", source);
        session.setAttribute("userBot", userBot);
        session.setAttribute("sourceId", sourceId);
        session.setAttribute("userId", userId);
        return "/app/statistic/source";
    }

    @GetMapping("/source-date")
    public String getSourceDate(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam("stopDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate stopDate,
                                Model model, HttpSession session) {
        long userId = (Long) session.getAttribute("userId");
        int sourceId = (Integer) session.getAttribute("sourceId");
        LocalDateTime startDateTime = convertToLocalDateTimeStartOfDay(startDate);
        LocalDateTime stopDateTime = convertToLocalDateTimeEndOfDay(stopDate);
        List<HistoryOrder> historyOrderList = historyOrderRepository.findAllBySourceAndUserIdDate(sourceId, userId, startDateTime, stopDateTime);
        Source source = sourceService.findById(sourceId);
        SourceStat sourceStat = statisticService.getSourceStatistic(historyOrderList, source, false);
        model.addAttribute("sourceStat", sourceStat);
        model.addAttribute("source", source);
        return "/app/statistic/source";
    }

    @GetMapping("/source-open-time")
    public String getSourceOpenTime(@RequestParam int sourceId, boolean userBot, Model model, @AuthenticationPrincipal UserDetails authenticatedUser, HttpSession session) {
        long userId = 1000L;
        if (!userBot) {
            userId = userService.getUserBasic(authenticatedUser.getUsername()).getId();
        }
        List<HistoryOrder> historyOrderList = historyOrderRepository.findAllBySourceAndUserId(sourceId, userId);
        Source source = sourceService.findById(sourceId);
        SourceStat sourceStat = statisticService.getSourceStatistic(historyOrderList, source, true);
        model.addAttribute("sourceStat", sourceStat);
        model.addAttribute("source", source);
        session.setAttribute("userBot", userBot);
        session.setAttribute("sourceId", sourceId);
        session.setAttribute("userId", userId);
        return "/app/statistic/source";
    }


    @ModelAttribute("sourceList")
    public Source[] sourceList() {
        return sourceService.findAll().toArray(new Source[0]);
    }


    public static LocalDateTime convertToLocalDateTimeStartOfDay(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    public static LocalDateTime convertToLocalDateTimeEndOfDay(LocalDate localDate) {
        return localDate.atTime(LocalTime.MAX);
    }

}
