package com.kramp.aggregator.service;

import com.kramp.aggregator.domain.model.CustomerSegment;
import com.kramp.aggregator.domain.model.Price;
import com.kramp.aggregator.domain.model.Product;
import com.kramp.aggregator.domain.model.ProductView;
import com.kramp.aggregator.domain.model.Stock;
import com.kramp.aggregator.domain.ports.AvailabilityPort;
import com.kramp.aggregator.domain.ports.CatalogPort;
import com.kramp.aggregator.domain.ports.CustomerPort;
import com.kramp.aggregator.domain.ports.PricingPort;
import com.kramp.aggregator.exception.CatalogServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.StructuredTaskScope.*;

@Service
@RequiredArgsConstructor
public class ProductAggregationService {

    private final CatalogPort catalogPort;
    private final PricingPort pricingPort;
    private final AvailabilityPort availabilityPort;
    private final CustomerPort customerPort;

    public ProductView getProduct(
            String productId,
            String market,
            String customerId
    ) throws TimeoutException {

        try (var scope = new ShutdownOnFailure()) {

            var catalogTask = scope.fork(() ->
                    catalogPort.getProduct(productId, market)
            );

            var priceTask = scope.fork(() -> {
                try {
                    return pricingPort.getPrice(productId, market, customerId);
                } catch (Exception e) {
                    return new Price(null, null, false);
                }
            });

            var availabilityTask = scope.fork(() -> {
                try {
                    return availabilityPort.getStock(productId);
                } catch (Exception e) {
                    return new Stock(false, null);
                }
            });

            var customerTask = customerId != null
                    ? scope.fork(() -> {
                try {
                    return customerPort.getCustomer(customerId);
                } catch (Exception e) {
                    return null;
                }
            })
                    : null;

            scope.joinUntil(Instant.now().plusMillis(200));

            Product product = catalogTask.get();

            Price price = priceTask.state() == Subtask.State.SUCCESS
                    ? priceTask.get()
                    : new Price(null, null, false);

            Stock stock = availabilityTask.state() == Subtask.State.SUCCESS
                    ? availabilityTask.get()
                    : new Stock(false, null);

            CustomerSegment customer =
                    customerTask != null ? customerTask.get() : null;

            return new ProductView(
                    product.id(),
                    product.name(),
                    product.description(),
                    price,
                    stock,
                    customer != null ? customer.segment() : null
            );
        } catch (IllegalStateException e) {
            throw new CatalogServiceException("Catalog service unavailable");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new TimeoutException();
        }
    }
}
