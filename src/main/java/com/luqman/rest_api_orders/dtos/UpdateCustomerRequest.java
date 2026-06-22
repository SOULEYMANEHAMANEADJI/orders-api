package com.luqman.rest_api_orders.dtos;

public record UpdateCustomerRequest(
        String firstName,
        String lastName,
        String address,
        Boolean vip
) {}

