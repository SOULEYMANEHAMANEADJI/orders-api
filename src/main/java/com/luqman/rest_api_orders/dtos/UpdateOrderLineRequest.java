package com.luqman.rest_api_orders.dtos;

import jakarta.validation.constraints.Positive;

public record UpdateOrderLineRequest(
        @Positive(message = "Quantity must be positive")
        Integer quantity
) {}

