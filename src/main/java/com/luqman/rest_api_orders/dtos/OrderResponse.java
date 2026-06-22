package com.luqman.rest_api_orders.dtos;

import com.luqman.rest_api_orders.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderLineResponse> orderLines
) {}

