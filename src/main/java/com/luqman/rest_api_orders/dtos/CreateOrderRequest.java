package com.luqman.rest_api_orders.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(@NotNull @JsonAlias("clientId") Long customerId) {
}



