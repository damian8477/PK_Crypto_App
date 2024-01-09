package pl.coderslab.controller.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.coderslab.entity.orders.HistoryOrder;
import pl.coderslab.repository.HistoryOrderRepository;

import java.util.List;

@Controller
@RequestMapping("/app/statistic")
@RequiredArgsConstructor
public class StatisticController {
    public final HistoryOrderRepository historyOrderRepository;

    @GetMapping("/data")
    public String getData(){
        List<HistoryOrder> historyOrderList = historyOrderRepository.findAllBySourceAndUserId(3, 1000L);
        return "/app/statistic/data";
    }
}
