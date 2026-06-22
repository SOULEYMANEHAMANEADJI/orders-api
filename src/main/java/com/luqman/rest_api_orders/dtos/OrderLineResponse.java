package com.luqman.rest_api_orders.dtos;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}

