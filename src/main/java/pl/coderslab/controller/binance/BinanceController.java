package pl.coderslab.controller.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.binance.client.model.enums.OrderSide;
import pl.coderslab.binance.client.model.enums.OrderType;
import pl.coderslab.binance.client.model.enums.PositionSide;
import pl.coderslab.entity.orders.Symbol;
import pl.coderslab.entity.user.User;
import pl.coderslab.enums.CashType;
import pl.coderslab.model.AlertSetting;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.model.CryptoName;
import pl.coderslab.model.OwnSignal;
import pl.coderslab.repository.SymbolRepository;
import pl.coderslab.service.binance.BinanceService;
import pl.coderslab.service.binance.OwnSignalService;
import pl.coderslab.service.entity.SymbolService;
import pl.coderslab.service.entity.UserService;
import pl.coderslab.service.telegram.AlertService;

import javax.validation.Valid;

import java.util.Arrays;

import static java.util.Objects.isNull;

@Controller
@RequestMapping("/app/binance")
@RequiredArgsConstructor
public class BinanceController {
    private final BinanceService binanceService;
    private final SymbolRepository symbolRepository;
    private final SymbolService symbolService;
    private final UserService userService;
    private final OwnSignalService ownSignalService;

    @GetMapping("/symbol-list")
    public String getSymbolList(Model model) {
        model.addAttribute("symbol", new Symbol());
        model.addAttribute("symbols", binanceService.getSymbols());
        return "app/binance/symbol/symbol-list";
    }

    @PostMapping("/add-symbol")
    public String addSymbol(@Valid @ModelAttribute("symbol") Symbol symbol, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("symbol", symbol);
            model.addAttribute("symbols", binanceService.getSymbols());
            return "app/binance/symbol/symbol-list";
        }
        try {
            symbolService.checkSymbol(symbol);
            symbolRepository.save(symbol);
            return "redirect:/app/binance/symbol-list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("symbol", symbol);
            bindingResult.rejectValue("name", e.getMessage(), e.getMessage());
            model.addAttribute("symbols", binanceService.getSymbols());
            return "app/binance/symbol/symbol-list";
        }
    }

    @GetMapping("/delete-symbol")
    public String getDelete(@RequestParam int symbolId, Model model) {
        model.addAttribute("symbolId", symbolId);
        return "app/binance/symbol/symbol-delete";
    }

    @PostMapping("/delete-symbol")
    public String deleteUserSetting(@RequestParam int symbolId) {
        symbolRepository.deleteById(symbolId);
        return "redirect:/app/binance/symbol-list";
    }

    @GetMapping("/open-symbol")
    public String getOpenSymbol(@RequestParam int symbolId, Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        CryptoName symbol = binanceService.getSymbols(symbolId);
        model.addAttribute("symbol", symbol);
        model.addAttribute("user", userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername()));
        model.addAttribute("ownSignal", new OwnSignal(symbol.getSymbol()));
        return "app/binance/symbol/open-symbol";
    }

    @PostMapping("/open-symbol")
    public String openSymbol(@Valid OwnSignal ownSignal, BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername()));
            model.addAttribute("ownSignal", ownSignal);
            return "app/binance/symbol/open-symbol";
        }
        System.out.println("allt -------------" + ownSignal);
        try {
            User user = userService.getUserWithUserSettingsByUserName(authenticatedUser.getUsername());
            SyncRequestClient syncRequestClient = binanceService.sync(ownSignal.getUserSetting());
            ownSignalService.checkOwnSignal(ownSignal, syncRequestClient);
            CommonSignal signal = ownSignalService.createCommonSignal(user, ownSignal, ownSignal.getStrategySetting(), ownSignal.getUserSetting(), syncRequestClient);
            binanceService.createOrder(signal, user, ownSignal.getUserSetting(), syncRequestClient);
        } catch (IllegalArgumentException e) {
            //todo komunikaty co jest nie tak
        }

        System.out.println("new -------------" + ownSignal);
        //todo sprawdzenie sygnalu, nastepnie wsystawienie i przeniesienie  na liste zlecen uzytkownika
        return "redirect:/app/binance/symbol/symbol-list";
    }


    @ModelAttribute("orderTypes")
    public OrderType[] orderTypes() {
        return OrderType.values();
    }

    @ModelAttribute("orderSides")
    public OrderSide[] orderSides() {
        return OrderSide.values();
    }

    @ModelAttribute("positionSides")
    public PositionSide[] positionSides() {
        PositionSide[] positionSides = PositionSide.values();
        positionSides = Arrays.stream(positionSides).filter(s -> !s.equals(PositionSide.BOTH)).toArray(PositionSide[]::new);
        return positionSides;
    }

    @ModelAttribute("cashTypes")
    public CashType[] cashTypes() {
        return CashType.values();
    }


}
