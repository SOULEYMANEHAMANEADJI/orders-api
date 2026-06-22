package com.luqman.rest_api_orders;

import com.luqman.rest_api_orders.dtos.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("DTO Validation Tests")
class DTOValidationTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should accept valid CreateProductRequest")
    void testValidCreateProductRequest() {
        CreateProductRequest request = new CreateProductRequest(
                "Laptop",
                "High-performance laptop",
                new BigDecimal("1.50"),
                new BigDecimal("999.99"),
                10
        );

        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Should have no violations");
    }

    @Test
    @DisplayName("Should reject CreateProductRequest with null name")
    void testCreateProductRequestNullName() {
        CreateProductRequest request = new CreateProductRequest(
                null,
                "Description",
                new BigDecimal("1.50"),
                new BigDecimal("999.99"),
                10
        );

        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Should have violations");
    }

    @Test
    @DisplayName("Should reject CreateProductRequest with negative price")
    void testCreateProductRequestNegativePrice() {
        CreateProductRequest request = new CreateProductRequest(
                "Laptop",
                "Description",
                new BigDecimal("1.50"),
                new BigDecimal("-99.99"),
                10
        );

        Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Should have violations for negative price");
    }

    @Test
    @DisplayName("Should accept valid CreateCustomerRequest")
    void testValidCreateCustomerRequest() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "John",
                "Doe",
                "123 Main St",
                false
        );

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Should have no violations");
    }

    @Test
    @DisplayName("Should reject CreateCustomerRequest with null firstName")
    void testCreateCustomerRequestNullFirstName() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                null,
                "Doe",
                "123 Main St",
                false
        );

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Should have violations");
    }

    @Test
    @DisplayName("Should accept valid UpdateOrderLineRequest")
    void testValidUpdateOrderLineRequest() {
        UpdateOrderLineRequest request = new UpdateOrderLineRequest(5);

        Set<ConstraintViolation<UpdateOrderLineRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Should have no violations");
    }

    @Test
    @DisplayName("Should reject UpdateOrderLineRequest with zero quantity")
    void testUpdateOrderLineRequestZeroQuantity() {
        UpdateOrderLineRequest request = new UpdateOrderLineRequest(0);

        Set<ConstraintViolation<UpdateOrderLineRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Should have violations for zero quantity");
    }

    @Test
    @DisplayName("Should reject UpdateOrderLineRequest with negative quantity")
    void testUpdateOrderLineRequestNegativeQuantity() {
        UpdateOrderLineRequest request = new UpdateOrderLineRequest(-5);

        Set<ConstraintViolation<UpdateOrderLineRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Should have violations for negative quantity");
    }

    @Test
    @DisplayName("Should accept valid AddOrderLineRequest")
    void testValidAddOrderLineRequest() {
        AddOrderLineRequest request = new AddOrderLineRequest(1L, 5);

        Set<ConstraintViolation<AddOrderLineRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Should have no violations");
    }

    @Test
    @DisplayName("Should reject AddOrderLineRequest with negative quantity")
    void testAddOrderLineRequestNegativeQuantity() {
        AddOrderLineRequest request = new AddOrderLineRequest(1L, -5);

        Set<ConstraintViolation<AddOrderLineRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Should have violations");
    }
}


