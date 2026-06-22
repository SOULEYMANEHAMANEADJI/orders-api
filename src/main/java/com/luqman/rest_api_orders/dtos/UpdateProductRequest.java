package com.luqman.rest_api_orders.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record UpdateProductRequest(
        String name,
        String description,

        @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be positive")
        BigDecimal weight,

        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        BigDecimal price,

        @Min(value = 0, message = "Stock cannot be negative")
        Integer stock
) {}

