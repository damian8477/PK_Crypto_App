package pl.coderslab.configuration;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Data
public class EnvironmentVariable {

    @Value("${BINANCE_SECRET_ENCRYPTION_KEY}")
    private String binanceSecret;
}
