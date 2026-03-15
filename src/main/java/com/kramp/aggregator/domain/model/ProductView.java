package com.kramp.aggregator.domain.model;

public record ProductView(String productId,
                          String name,
                          String description,
                          Price price,
                          Stock availability,
                          String customerSegment) {
}
