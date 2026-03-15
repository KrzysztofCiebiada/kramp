package com.kramp.aggregator.controller;

import com.kramp.aggregator.domain.model.Price;
import com.kramp.aggregator.domain.model.ProductView;
import com.kramp.aggregator.domain.model.Stock;
import com.kramp.aggregator.service.ProductAggregationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductAggregationService service;

    @Test
    void shouldReturnProduct() throws Exception {

        ProductView view = new ProductView(
                "1",
                "Pump",
                "desc",
                new Price(BigDecimal.valueOf(100L),"EUR",true),
                new Stock(true,10),
                null
        );

        when(service.getProduct(any(),any(),any()))
                .thenReturn(view);

        mockMvc.perform(get("/products/1")
                        .param("market","pl-PL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pump"));
    }
}
