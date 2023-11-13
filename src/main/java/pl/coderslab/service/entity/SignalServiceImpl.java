package pl.coderslab.service.entity;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.Signal;
import pl.coderslab.interfaces.SignalService;
import pl.coderslab.repository.SourceRepository;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.repository.SignalRepository;
import pl.coderslab.service.telegram.RequestTelegramService;

@Service
@RequiredArgsConstructor
public class SignalServiceImpl implements SignalService {
    private final SignalRepository signalRepository;
    private final SourceRepository sourceRepository;

    private static final Logger logger = LoggerFactory.getLogger(RequestTelegramService.class);

    @Override
    public void save(Signal signal) {
        signalRepository.save(signal);
    }

    @Override
    public Signal saveSignalFromCommonSignal(CommonSignal commonSignal) {
        try {
            Signal signal = fillSignal(commonSignal);
            return signalRepository.save(signal);
        } catch (Exception e) {
            logger.error("Error during save signal to db " + e);
        }
        return null;
    }

    private Signal fillSignal(CommonSignal commonSignal) {
        return Signal.builder()
                .symbol(commonSignal.getSymbol())
                .entryPrice(commonSignal.getEntryPrice().get(0))
                .entryPrice2(commonSignal.getEntryPrice().get(1))
                .entryPrice3(commonSignal.getEntryPrice().get(2))
                .takeProfit1(commonSignal.getTakeProfit().get(0))
                .takeProfit2(commonSignal.getTakeProfit().get(1))
                .takeProfit3(commonSignal.getTakeProfit().get(2))
                .takeProfit4(commonSignal.getTakeProfit().get(3))
                .takeProfit5(commonSignal.getTakeProfit().get(4))
                .stopLoss(commonSignal.getStopLoss().get(0))
                .source(sourceRepository.findByName(commonSignal.getSourceName()))
                .build();
    }


}
