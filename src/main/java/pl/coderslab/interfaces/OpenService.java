package pl.coderslab.interfaces;

import org.springframework.stereotype.Service;
import pl.coderslab.model.CommonSignal;


public interface OpenService {
    void newSignal(CommonSignal signal);
}
