package com.luqman.rest_api_orders.dtos;

import java.math.BigDecimal;

public record ArticleResponseModel(
        Long id,
        String libelle,
        String description,
        double poids,
        BigDecimal prix
) {
}
