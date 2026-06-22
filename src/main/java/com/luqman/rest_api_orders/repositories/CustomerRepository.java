package com.luqman.rest_api_orders.repositories;

import com.luqman.rest_api_orders.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
