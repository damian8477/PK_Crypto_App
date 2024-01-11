package pl.coderslab.controller.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.entity.orders.HistoryOrder;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.interfaces.SourceService;
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

    @GetMapping("/data")
    public String getData() {
        List<HistoryOrder> historyOrderList = historyOrderRepository.findAllBySourceAndUserId(3, 1000L);
        return "/app/statistic/data";
    }

    @GetMapping("/source")
    public String getSource(@RequestParam int sourceId, Model model) {
        List<HistoryOrder> historyOrderList = historyOrderRepository.findAllBySourceAndUserId(sourceId, 1000L);
        Source source = sourceService.findById(sourceId);
        SourceStat sourceStat = statisticService.getSourceStatistic(historyOrderList);
        return "/app/statistic/data";
    }


}
