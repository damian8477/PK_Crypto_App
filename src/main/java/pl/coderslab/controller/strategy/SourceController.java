package pl.coderslab.controller.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.entity.strategy.Source;
import pl.coderslab.enums.MarginType;
import pl.coderslab.interfaces.SourceService;
import pl.coderslab.interfaces.SymbolService;
import pl.coderslab.repository.SourceRepository;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.repository.SymbolRepository;

import javax.validation.Valid;
import java.util.List;

import static java.util.Objects.isNull;

@Controller
@RequestMapping("/app/source")
@RequiredArgsConstructor
public class SourceController {
    private final SourceService sourceService;
    private final SymbolService symbolService;

    @GetMapping("/list")
    public String getStrategyList(Model model) {
        model.addAttribute("sources", sourceService.findAll());
        return "/app/source/list";
    }

    @GetMapping("/add")
    public String getAddStrategy(Model model) {
        model.addAttribute("source", new Source());
        return "/app/source/add";
    }

    @PostMapping("/add")
    public String addStrategy(@Valid @ModelAttribute("source") Source source, BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (bindingResult.hasErrors()) {
            return "app/source/add";
        }
        if (authenticatedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            source.setAdminStrategy(true);
        }
        sourceService.save(source);
        return "redirect:/app/source/list";
    }

    @GetMapping("/delete")
    public String getDeleteView(@RequestParam int sourceId, Model model) {
        model.addAttribute("sourceId", sourceId);
        return "/app/source/delete";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam int sourceId) {
        sourceService.deleteById(sourceId);
        return "redirect:/app/source/list";
    }

    @GetMapping("/edit")
    public String getEditView(@RequestParam int sourceId, Model model) {
        model.addAttribute("source", sourceService.findById(sourceId));
        return "app/source/edit";
    }

    @PostMapping("/edit")
    public String editStrategy(@Valid @ModelAttribute("source") Source source, BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (bindingResult.hasErrors()) {
            return "app/source/edit";
        }
        if (authenticatedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            source.setAdminStrategy(true);
        }
        sourceService.save(source);
        return "redirect:/app/source/list";
    }

    @GetMapping("/symbols")
    public String getSymbolsView(@RequestParam int sourceId, Model model) {
        model.addAttribute("sourceId", sourceId);
        model.addAttribute("symbols", sourceService.findById(sourceId).getSymbols());
        return "app/source/symbols";
    }

    @PostMapping("/symboladd")
    public String addSymbolsView(@RequestParam int sourceId, @RequestParam int symbolId, Model model) {
        Source source = sourceService.findById(sourceId);
        Symbol symbol = symbolService.findById(symbolId);
        if (!isNull(source) && !isNull(symbol)) {
            if (source.getSymbols().stream().filter(s -> s.getName().equals(symbol.getName())).toList().isEmpty()) {
                source.addSymbol(symbol);
                sourceService.save(source);
            }
            return "redirect:/app/source/symbols?sourceId=" + sourceId;
        } else {
            return "redirect:/app/source/list";
        }
    }

    @PostMapping("/delete-symbol")
    public String deleteSymbol(@RequestParam int symbolId, @RequestParam int sourceId) {
        Source source = sourceService.findById(sourceId);
        Symbol symbol = symbolService.findById(symbolId);
        source.deleteSymbol(symbol);
        sourceService.save(source);
        return "redirect:/app/source/symbols?sourceId=" + sourceId;
    }


    @ModelAttribute("symbolList")
    public Symbol[] symbolList() {
        return symbolService.findAll().toArray(new Symbol[0]);
    }


}
