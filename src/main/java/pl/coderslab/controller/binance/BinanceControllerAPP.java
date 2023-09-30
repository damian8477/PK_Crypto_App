package pl.coderslab.controller.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.service.binance.BinanceService;

@Controller
@RequestMapping("/app/binance")
@RequiredArgsConstructor
public class BinanceControllerAPP {
    private final BinanceService binanceService;

    @GetMapping("/symbol-list")
    public String getSymbolList(Model model) {
        model.addAttribute("symbols", binanceService.getSymbols());
        return "/app/symbol-list";
    }

}
