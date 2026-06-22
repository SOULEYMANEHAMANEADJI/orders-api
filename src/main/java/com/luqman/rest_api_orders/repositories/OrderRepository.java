package com.luqman.rest_api_orders.repositories;

import com.luqman.rest_api_orders.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
