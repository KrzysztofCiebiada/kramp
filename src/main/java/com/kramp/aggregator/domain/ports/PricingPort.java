package com.kramp.aggregator.domain.ports;

import com.kramp.aggregator.domain.model.Price;

public interface PricingPort {
    public Price getPrice(String productId, String market, String customerId);
}
