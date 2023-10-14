package pl.coderslab.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "binance.api.var")
@Data
public class BinanceConfigProperties {
    private String apiKey;
    private String secretKey;
}

