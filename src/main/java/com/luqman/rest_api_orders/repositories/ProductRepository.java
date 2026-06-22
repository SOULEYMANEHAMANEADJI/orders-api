package com.luqman.rest_api_orders.repositories;

import com.luqman.rest_api_orders.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
