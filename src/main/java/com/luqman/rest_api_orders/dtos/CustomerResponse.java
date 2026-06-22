package com.luqman.rest_api_orders.dtos;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String address,
        boolean vip
) {
}

