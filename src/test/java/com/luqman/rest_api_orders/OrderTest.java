package com.luqman.rest_api_orders;

import com.luqman.rest_api_orders.entities.Customer;
import com.luqman.rest_api_orders.entities.Order;
import com.luqman.rest_api_orders.entities.OrderLine;
import com.luqman.rest_api_orders.entities.Product;
import com.luqman.rest_api_orders.enums.OrderStatus;
import com.luqman.rest_api_orders.exceptions.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class OrderTest {

    private Product createProduct(BigDecimal price) {
        Product product = new Product();
        product.setStock(10);
        product.setPrice(price);
        return product;
    }

    private Order createOrder() {
        Order order = new Order();
        Customer customer = new Customer();
        customer.setVip(false);
        order.setCustomer(customer);
        return order;
    }

    @Test
    public void addLineToOrder_valid() {
        Product product = createProduct(new BigDecimal("100.00"));
        Order order = createOrder();

        order.addLineToOrder(product, 2);

        Assertions.assertEquals(1, order.getOrderLines().size());
            OrderLine orderLine = order.getOrderLines().get(0);
        Assertions.assertEquals(2, orderLine.getQuantity());
        Assertions.assertEquals(new BigDecimal("100.00"), orderLine.getUnitPrice());
        Assertions.assertEquals(8, product.getStock());
    }

    @Test
    public void addLineToOrder_invalidQuantity() {
        Product product = createProduct(new BigDecimal("100.00"));
        Order order = createOrder();

        BusinessException exception = Assertions.assertThrows(
                BusinessException.class,
                () -> order.addLineToOrder(product, -1));

        Assertions.assertEquals("Quantity must be greater than 0", exception.getMessage());
    }

    @Test
    public void validateOrder_shouldApplyVipDiscount() {
        Product product = createProduct(new BigDecimal("100.00"));
        Order order = new Order();
        Customer customer = new Customer();
        customer.setVip(true);
        order.setCustomer(customer);
        order.addLineToOrder(product, 2);

        order.validateOrder();

        Assertions.assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        Assertions.assertEquals(new BigDecimal("180.0000"), order.getTotalAmount());
    }
}

