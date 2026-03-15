package com.kramp.aggregator.resilience;

import com.kramp.aggregator.domain.model.Product;
import com.kramp.aggregator.domain.model.ProductView;
import com.kramp.aggregator.domain.ports.AvailabilityPort;
import com.kramp.aggregator.domain.ports.CatalogPort;
import com.kramp.aggregator.domain.ports.CustomerPort;
import com.kramp.aggregator.domain.ports.PricingPort;
import com.kramp.aggregator.service.ProductAggregationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FailureHandlingTest {

    CatalogPort catalogPort = mock(CatalogPort.class);
    PricingPort pricingPort = mock(PricingPort.class);
    AvailabilityPort availabilityPort = mock(AvailabilityPort.class);
    CustomerPort customerPort = mock(CustomerPort.class);

    ProductAggregationService service =
            new ProductAggregationService(
                    catalogPort,
                    pricingPort,
                    availabilityPort,
                    customerPort
            );

    @Test
    void shouldReturnUnknownStockWhenAvailabilityFails() throws Exception {
        Product product = new Product("1","Pump","desc");

        when(catalogPort.getProduct(any(),any()))
                .thenReturn(product);

        when(availabilityPort.getStock(any()))
                .thenThrow(new RuntimeException());

        ProductView view = service.getProduct("1","pl-PL",null);

        assertFalse(view.availability().inStock());
    }
}
