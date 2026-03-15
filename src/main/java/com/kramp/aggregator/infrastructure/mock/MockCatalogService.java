package com.kramp.aggregator.infrastructure.mock;

import com.kramp.aggregator.domain.model.Product;
import com.kramp.aggregator.domain.ports.CatalogPort;
import com.kramp.aggregator.infrastructure.generator.ProductDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.kramp.aggregator.helper.FailureSimulator.simulateFailure;
import static com.kramp.aggregator.helper.FailureSimulator.simulateLatency;

@Service
@RequiredArgsConstructor
public class MockCatalogService implements CatalogPort {

    private final ProductDataGenerator generator;

    @Override
    public Product getProduct(String productId, String market) {
            simulateLatency(50);
            simulateFailure(0.999);

            return generator.generateProduct(productId, market);
    }
}
