package pl.coderslab.controller.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.repository.SymbolRepository;
import pl.coderslab.service.binance.BinanceService;
import pl.coderslab.service.entity.SymbolService;

import javax.validation.Valid;

@Controller
@RequestMapping("/app/binance")
@RequiredArgsConstructor
public class BinanceController {
    private final BinanceService binanceService;
    private final SymbolRepository symbolRepository;
    private final SymbolService symbolService;

    @GetMapping("/symbol-list")
    public String getSymbolList(Model model) {
        model.addAttribute("symbol", new Symbol());
        model.addAttribute("symbols", binanceService.getSymbols());
        return "app/symbol/symbol-list";
    }

    @PostMapping("/add-symbol")
    public String addSymbol(@Valid @ModelAttribute("symbol") Symbol symbol, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("symbol", symbol);
            model.addAttribute("symbols", binanceService.getSymbols());
            return "app/symbol/symbol-list";
        }
        try{
            System.out.println(symbol);
            symbolService.checkSymbol(symbol);
            System.out.println(symbol);
            symbolRepository.save(symbol);
            return "redirect:/app/binance/symbol-list";
        }catch (IllegalArgumentException e){
            model.addAttribute("symbol", symbol);
            bindingResult.rejectValue("name", e.getMessage(), e.getMessage());
            model.addAttribute("symbols", binanceService.getSymbols());
            System.out.println(e.getMessage());
            return "app/symbol/symbol-list";
        }
    }

    @GetMapping("/delete-symbol")
    public String getDelete(@RequestParam int symbolId, Model model){
        model.addAttribute("symbolId", symbolId);
        return "/app/symbol/symbol-delete";
    }

    @PostMapping("/delete-symbol")
    public String deleteUserSetting(@RequestParam int symbolId){
        symbolRepository.deleteById(symbolId);
        return "redirect:/app/binance/symbol-list";
    }



}
