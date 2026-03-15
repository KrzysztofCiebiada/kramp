package com.kramp.aggregator.domain.ports;

import com.kramp.aggregator.domain.model.Stock;

public interface AvailabilityPort {
    Stock getStock(String productId);
}
