package pl.coderslab.service.telegram.vip;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.interfaces.OpenService;
import pl.coderslab.interfaces.SignalService;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.service.parser.VipSignalParserService;

@Service
@RequiredArgsConstructor
public class RequestSignalVipServiceImpl {
    private final VipSignalParserService vipSignalParserService;
    private final SignalService signalService;
    private final OpenService openService;

    private static final Logger logger = LoggerFactory.getLogger(RequestSignalVipServiceImpl.class);

    public void newMessage(String message){
        if(message.contains("BUY") || message.contains("SELL")){
            CommonSignal signal = vipSignalParserService.parseSignalMessage(message);
            logger.info("Signal Vip: " + signal);
            signal.setSignal(signalService.saveSignalFromCommonSignal(signal));
            openService.newSignal(signal);

        }
    }



}
