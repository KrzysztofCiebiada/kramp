package com.kramp.aggregator.domain.model;

import java.math.BigDecimal;

public record Price(BigDecimal value,
                    String currency,
                    boolean available) {
}
