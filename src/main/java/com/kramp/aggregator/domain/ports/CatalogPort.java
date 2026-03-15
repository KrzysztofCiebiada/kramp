package com.kramp.aggregator.domain.ports;

import com.kramp.aggregator.domain.model.Product;

public interface CatalogPort {
    Product getProduct(String productId, String market);
}
