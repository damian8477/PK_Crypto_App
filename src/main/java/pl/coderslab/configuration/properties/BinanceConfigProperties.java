package pl.coderslab.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "binance.api.var")
@Data
public class BinanceConfigProperties {
    private String apiKey;
    private String secretKey;
}

