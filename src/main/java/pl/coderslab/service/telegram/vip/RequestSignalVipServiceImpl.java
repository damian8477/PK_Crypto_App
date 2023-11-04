package pl.coderslab.service.telegram.vip;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderslab.model.CommonSignal;
import pl.coderslab.service.entity.SignalServiceImpl;
import pl.coderslab.service.parser.VipSignalParserService;

@Service
@RequiredArgsConstructor
public class RequestSignalVipServiceImpl {
    private final VipSignalParserService vipSignalParserService;

    private static final Logger logger = LoggerFactory.getLogger(RequestSignalVipServiceImpl.class);

    public void newMessage(String message){
        if(message.contains("BUY") || message.contains("SELL")){
            CommonSignal signal = vipSignalParserService.parseSignalMessage(message);
            System.out.println(signal);
        }
    }



}
