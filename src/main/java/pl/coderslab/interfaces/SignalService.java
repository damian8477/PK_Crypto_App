package pl.coderslab.interfaces;

import pl.coderslab.entity.orders.Signal;
import pl.coderslab.model.CommonSignal;

public interface SignalService {
    void save(Signal signal);
    Signal saveSignalFromCommonSignal(CommonSignal commonSignal);
}
