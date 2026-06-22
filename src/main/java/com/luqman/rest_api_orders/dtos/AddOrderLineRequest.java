package com.luqman.rest_api_orders.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddOrderLineRequest(@NotNull @JsonAlias("articleId") Long productId, @Positive @JsonAlias({"quantite", "quantity"}) int quantity) {
}



