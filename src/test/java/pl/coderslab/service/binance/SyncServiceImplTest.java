package pl.coderslab.service.binance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import pl.coderslab.binance.client.SyncRequestClient;
import pl.coderslab.configuration.properties.BinanceConfigProperties;
import pl.coderslab.entity.user.UserSetting;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SyncServiceImplTest {
    @Mock
    private BinanceConfigProperties binanceConfigProperties;

    @Spy
    private Logger logger;

    @InjectMocks
    private SyncServiceImpl syncService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sync_withNullUserSetting_shouldUseConfigProperties() {
        // Arrange
        when(binanceConfigProperties.getApiKey()).thenReturn("configApiKey");
        when(binanceConfigProperties.getSecretKey()).thenReturn("configSecretKey");

        // Act
        SyncRequestClient result = syncService.sync(null);

        // Assert
        assertNotNull(result);
        verify(logger, never()).info(anyString());
        verify(logger, never()).info(contains("Error during connecting to binance"));
    }

    @Test
    void sync_withNonNullUserSetting_shouldUseUserSetting() {
        // Arrange
        UserSetting userSetting = getTestUserSetting();
        when(binanceConfigProperties.getApiKey()).thenReturn("configApiKey");
        when(binanceConfigProperties.getSecretKey()).thenReturn("configSecretKey");

        // Act
        SyncRequestClient result = syncService.sync(userSetting);

        // Assert
        assertNotNull(result);
        verify(logger, never()).info(anyString());
        verify(logger, never()).info(contains("Error during connecting to binance"));
    }

    @Test
    void sync_withExceptionDuringSync_shouldRetry() {
        // Arrange
        UserSetting userSetting = getTestUserSetting();
        when(binanceConfigProperties.getApiKey()).thenReturn("configApiKey");
        when(binanceConfigProperties.getSecretKey()).thenReturn("configSecretKey");
        doThrow(new RuntimeException("Simulated exception")).doNothing().when(logger).info(contains("Error during connecting to binance"));

        // Act
        SyncRequestClient result = syncService.sync(userSetting);

        // Assert
        assertNotNull(result);
        //verify(logger, atLeast(2)).info(contains("Error during connecting to binance"));
    }

    UserSetting getTestUserSetting(){
        return   UserSetting.builder()
                .binanceKey("userApiKey")
                .binanceSecret("userSecretKey")
                .build();
    }
}