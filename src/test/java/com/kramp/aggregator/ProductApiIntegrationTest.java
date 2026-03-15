package com.kramp.aggregator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturnProductFromMocks() throws Exception {

        mockMvc.perform(get("/products/123")
                        .param("market","pl-PL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("123"));
    }
}
