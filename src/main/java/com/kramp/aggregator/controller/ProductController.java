package com.kramp.aggregator.controller;

import com.kramp.aggregator.domain.model.ProductView;
import com.kramp.aggregator.service.ProductAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductAggregationService aggregationService;

    @GetMapping("/{id}")
    public ProductView getProduct(
            @PathVariable String id,
            @RequestParam String market,
            @RequestParam(required = false) String customerId
    ) throws Exception {

        return aggregationService.getProduct(
                id,
                market,
                customerId
        );
    }
}
