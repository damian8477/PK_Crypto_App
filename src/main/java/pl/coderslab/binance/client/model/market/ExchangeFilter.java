package pl.coderslab.binance.client.model.market;


import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.coderslab.binance.client.constant.BinanceApiConstants;

public class ExchangeFilter {

    private String filterType;

    private Long maxNumOrders;

    private Long maxNumAlgoOrders;

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public Long getMaxNumOrders() {
        return maxNumOrders;
    }

    public void setMaxNumOrders(Long maxNumOrders) {
        this.maxNumOrders = maxNumOrders;
    }

    public Long getMaxNumAlgoOrders() {
        return maxNumAlgoOrders;
    }

    public void setMaxNumAlgoOrders(Long maxNumAlgoOrders) {
        this.maxNumAlgoOrders = maxNumAlgoOrders;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE).append("filterType", filterType)
                .append("maxNumOrders", maxNumOrders).append("maxNumAlgoOrders", maxNumAlgoOrders).toString();
    }
}
