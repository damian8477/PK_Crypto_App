package pl.coderslab.model.statistic;

import java.math.BigDecimal;

public interface CommonStatInterface {
    BigDecimal getAccuracy();
    void setAccuracy(BigDecimal accuracy);

    int getCountWin();
    void setCountWin(int countWin);

    int getCountTrade();
    void setCountTrade(int countTrade);
}
