package com.kramp.aggregator.domain.ports;

import com.kramp.aggregator.domain.model.CustomerSegment;

public interface CustomerPort {
    CustomerSegment getCustomer(String customerId);
}
