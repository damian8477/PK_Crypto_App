package pl.coderslab.interfaces;

import pl.coderslab.entity.orders.Signal;
import pl.coderslab.model.CommonSignal;

public interface SignalService {

    Signal saveSignalFromCommonSignal(CommonSignal commonSignal);
}
