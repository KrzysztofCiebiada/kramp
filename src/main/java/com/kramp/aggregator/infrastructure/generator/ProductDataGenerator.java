package com.kramp.aggregator.infrastructure.generator;

import com.kramp.aggregator.domain.model.CustomerSegment;
import com.kramp.aggregator.domain.model.Price;
import com.kramp.aggregator.domain.model.Product;
import com.kramp.aggregator.domain.model.Stock;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Component
public class ProductDataGenerator {

    private static final Random RANDOM = new Random();

    private static final List<String> PRODUCT_NAMES = List.of(
            "Hydraulic Pump",
            "Fuel Filter",
            "Oil Pump",
            "Brake Disc",
            "Gearbox Housing",
            "Hydraulic Cylinder",
            "Fuel Injector"
    );

    private static final List<String> DESCRIPTIONS = List.of(
            "High performance industrial component",
            "OEM replacement part",
            "Heavy-duty agricultural machinery part",
            "Premium quality component",
            "Durable workshop-grade part"
    );

    private static final List<String> CUSTOMER_SEGMENTS = List.of(
            "dealer",
            "workshop",
            "wholesale"
    );

    public Product generateProduct(String productId, String market) {

        String name = deterministicValue(PRODUCT_NAMES, productId);
        String description = deterministicValue(DESCRIPTIONS, productId);

        return new Product(
                productId,
                name,
                description + " for market " + market
        );
    }

    public Price generatePrice(String productId, String customerId) {

        BigDecimal basePrice = BigDecimal.valueOf(50 + RANDOM.nextInt(200));

        if (customerId != null) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(0.9));
        }

        return new Price(
                basePrice,
                "EUR",
                true
        );
    }

    public Stock generateStock(String productId) {

        int quantity = RANDOM.nextInt(50);

        return new Stock(
                quantity > 0,
                quantity
        );
    }

    public CustomerSegment generateCustomer(String customerId) {

        String segment = deterministicValue(CUSTOMER_SEGMENTS, customerId);

        return new CustomerSegment(segment);
    }

    private String deterministicValue(List<String> values, String key) {

        int index = Math.abs(key.hashCode()) % values.size();
        return values.get(index);
    }
}
