package pl.coderslab.controller.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.coderslab.service.binance.BinanceService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
@RequestMapping("/app/binance")
@RequiredArgsConstructor
public class BinanceController {
    private final BinanceService binanceService;
    @GetMapping("/symbol-list")
    @ResponseBody
    public String getSymbolList(Model model){
        model.addAttribute("symbols", binanceService.getSymbols());
        System.out.println(binanceService.getSymbols().size());
        return binanceService.getSymbols().toString();
       // return "/binance/symbol-list";
    }
    @GetMapping("/symbols")
    @ResponseBody
    public String getSymbols(){
        try {
            // Tworzenie obiektu URL z adresem endpointu Binance API
            URL url = new URL("https://fapi.binance.com/fapi/v1/exchangeInfo");

            // Utworzenie połączenia HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Pobranie odpowiedzi
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Odczytanie odpowiedzi
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    break;
                }

                reader.close();

                // Wyświetlenie odpowiedzi (tu możesz przetwarzać dane JSON)
                System.out.println(response.toString());
            } else {
                System.out.println("Błąd HTTP: " + responseCode);
            }

            // Zamknięcie połączenia
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "xd";
    }

}
