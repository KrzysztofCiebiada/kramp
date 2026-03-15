package com.kramp.aggregator.infrastructure.mock;

import com.kramp.aggregator.infrastructure.generator.ProductDataGenerator;
import com.kramp.aggregator.domain.model.Price;
import com.kramp.aggregator.domain.ports.PricingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kramp.aggregator.helper.FailureSimulator.*;

@Service
@RequiredArgsConstructor
public class MockPricingService implements PricingPort {

    private final ProductDataGenerator generator;
    @Override
    public Price getPrice(String productId, String market, String customerId) {

        simulateLatency(80);
        simulateFailure(0.995);

        return generator.generatePrice(productId, customerId);
    }
}
