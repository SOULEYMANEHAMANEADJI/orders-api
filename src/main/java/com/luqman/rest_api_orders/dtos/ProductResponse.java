package com.luqman.rest_api_orders.dtos;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal weight,
        BigDecimal price,
        Integer stock
) {
}

