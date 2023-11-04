package pl.coderslab.service.entity;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.entity.orders.Signal;
import pl.coderslab.interfaces.SignalService;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.service.telegram.RequestTelegramService;

@Service
@RequiredArgsConstructor
public class SignalServiceImpl implements SignalService {

    private static final Logger logger = LoggerFactory.getLogger(RequestTelegramService.class);

    public void saveSignalFromCommonSignal(CommonSignal commonSignal){
        Signal signal = fillSignal(commonSignal);
    }

    private Signal fillSignal(CommonSignal commonSignal){
        return null;
    }


}
