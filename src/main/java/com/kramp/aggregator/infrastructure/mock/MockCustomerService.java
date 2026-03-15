package com.kramp.aggregator.infrastructure.mock;

import com.kramp.aggregator.infrastructure.generator.ProductDataGenerator;
import com.kramp.aggregator.domain.model.CustomerSegment;
import com.kramp.aggregator.domain.ports.CustomerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kramp.aggregator.helper.FailureSimulator.simulateFailure;
import static com.kramp.aggregator.helper.FailureSimulator.simulateLatency;

@Service
@RequiredArgsConstructor
public class MockCustomerService implements CustomerPort {

    private final ProductDataGenerator generator;
    @Override
    public CustomerSegment getCustomer(String customerId) {

        simulateLatency(60);
        simulateFailure(0.99);

        return generator.generateCustomer(customerId);
    }
}
