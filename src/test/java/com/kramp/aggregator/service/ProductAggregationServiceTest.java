package com.kramp.aggregator.service;

import com.kramp.aggregator.domain.model.Price;
import com.kramp.aggregator.domain.model.Product;
import com.kramp.aggregator.domain.model.ProductView;
import com.kramp.aggregator.domain.model.Stock;
import com.kramp.aggregator.domain.ports.AvailabilityPort;
import com.kramp.aggregator.domain.ports.CatalogPort;
import com.kramp.aggregator.domain.ports.CustomerPort;
import com.kramp.aggregator.domain.ports.PricingPort;
import com.kramp.aggregator.exception.CatalogServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductAggregationServiceTest {

    @Mock
    CatalogPort catalogPort;

    @Mock
    PricingPort pricingPort;

    @Mock
    AvailabilityPort availabilityPort;

    @Mock
    CustomerPort customerPort;

    @InjectMocks
    ProductAggregationService service;

    @Test
    void shouldReturnAggregatedProduct() throws Exception {

        Product product = new Product("1", "Pump", "desc");

        when(catalogPort.getProduct("1", "pl-PL"))
                .thenReturn(product);

        when(pricingPort.getPrice("1","pl-PL",null))
                .thenReturn(new Price(BigDecimal.valueOf(100L),"EUR",true));

        when(availabilityPort.getStock("1"))
                .thenReturn(new Stock(true,10));

        ProductView view = service.getProduct("1","pl-PL",null);

        assertEquals("Pump", view.name());
        assertTrue(view.price().available());
        assertTrue(view.availability().inStock());
    }

    @Test
    void shouldFailWhenCatalogFails() {

        when(catalogPort.getProduct(any(),any()))
                .thenThrow(new CatalogServiceException("catalog down"));

        assertThrows(
                CatalogServiceException.class,
                () -> service.getProduct("1","pl-PL",null)
        );
    }

    @Test
    void shouldTimeoutSlowServices() {

        Product product = new Product("1","Pump","desc");

        when(catalogPort.getProduct(any(),any()))
                .thenAnswer(inv -> {
                    Thread.sleep(500);
                    return product;
                });

        assertThrows(
                TimeoutException.class,
                () -> service.getProduct("1","pl-PL",null)
        );
    }

    @Test
    void shouldCallServicesConcurrently() throws Exception {

        Product product = new Product("1","Pump","desc");

        when(catalogPort.getProduct(any(),any()))
                .thenAnswer(inv -> {
                    Thread.sleep(100);
                    return product;
                });

        when(pricingPort.getPrice(any(),any(),any()))
                .thenAnswer(inv -> {
                    Thread.sleep(100);
                    return new Price(BigDecimal.valueOf(100L),"EUR",true);
                });

        when(availabilityPort.getStock(any()))
                .thenAnswer(inv -> {
                    Thread.sleep(100);
                    return new Stock(true,10);
                });

        long start = System.currentTimeMillis();

        service.getProduct("1","pl-PL",null);

        long duration = System.currentTimeMillis() - start;

        assertTrue(duration < 250);
    }
}
