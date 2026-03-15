package com.kramp.aggregator.infrastructure.mock;

import com.kramp.aggregator.infrastructure.generator.ProductDataGenerator;
import com.kramp.aggregator.domain.model.Stock;
import com.kramp.aggregator.domain.ports.AvailabilityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kramp.aggregator.helper.FailureSimulator.simulateFailure;
import static com.kramp.aggregator.helper.FailureSimulator.simulateLatency;

@Service
@RequiredArgsConstructor
public class MockAvailabilityService implements AvailabilityPort {

    private final ProductDataGenerator generator;
    @Override
    public Stock getStock(String productId) {

        simulateLatency(100);
        simulateFailure(0.98);

        return generator.generateStock(productId);
    }
}
