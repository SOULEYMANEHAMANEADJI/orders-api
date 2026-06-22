package com.luqman.rest_api_orders.controllers;

import com.luqman.rest_api_orders.dtos.CreateCustomerRequest;
import com.luqman.rest_api_orders.dtos.CustomerResponse;
import com.luqman.rest_api_orders.dtos.UpdateCustomerRequest;
import com.luqman.rest_api_orders.entities.Customer;
import com.luqman.rest_api_orders.services.CustomerService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/customers")
@RestController
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Operations on customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "List customers (paginated)")
    public ResponseEntity<Page<CustomerResponse>> getCustomers(Pageable pageable) {
        return ResponseEntity.ok(customerService.findAll(pageable).map(this::toResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer by id")
    public ResponseEntity<CustomerResponse> getClient(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(customerService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a customer")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = customerService.create(request);
        return ResponseEntity.status(201).body(toResponse(customer));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a customer")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest request) {
        Customer customer = customerService.update(id, request);
        return ResponseEntity.ok(toResponse(customer));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getAddress(),
                customer.isVip()
        );
    }
}
