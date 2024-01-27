package pl.coderslab.controller.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.coderslab.entity.strategy.rsi.RsiStrategy;
import pl.coderslab.repository.RsiStrategyRepository;

import javax.validation.Valid;

@Controller
@RequestMapping("/app/rsi")
@RequiredArgsConstructor
public class RsiStrategyController {
    private final RsiStrategyRepository rsiStrategyRepository;

    @PostMapping("/add")
    @ResponseBody
    public String addSymbol(@Valid RsiStrategy rsiStrategy, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("rsiStrategy", rsiStrategy);
        }
        rsiStrategy.setSymbol(rsiStrategy.getSymbol().toUpperCase());
        rsiStrategyRepository.save(rsiStrategy);
        return "Symbol added";
    }
}
