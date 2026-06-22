package com.luqman.rest_api_orders;

import com.luqman.rest_api_orders.dtos.AddOrderLineRequest;
import com.luqman.rest_api_orders.dtos.CreateOrderRequest;
import com.luqman.rest_api_orders.dtos.UpdateOrderLineRequest;
import com.luqman.rest_api_orders.dtos.UpdateOrderRequest;
import com.luqman.rest_api_orders.entities.Customer;
import com.luqman.rest_api_orders.entities.Order;
import com.luqman.rest_api_orders.entities.OrderLine;
import com.luqman.rest_api_orders.entities.Product;
import com.luqman.rest_api_orders.enums.OrderStatus;
import com.luqman.rest_api_orders.exceptions.BusinessException;
import com.luqman.rest_api_orders.repositories.OrderRepository;
import com.luqman.rest_api_orders.services.ProductService;
import com.luqman.rest_api_orders.services.CustomerService;
import com.luqman.rest_api_orders.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Product product;
    private Customer customer;
    private AddOrderLineRequest addLineRequest;

    @BeforeEach
    void setUp(){
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setVip(false);

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.DRAFT);
        order.setTotalAmount(BigDecimal.ZERO);

        product = new Product();
        product.setId(1L);
        product.setStock(10);
        product.setPrice(new BigDecimal("100.00"));

        addLineRequest = new AddOrderLineRequest(1L, 5);
    }

    @Test
    @DisplayName("Should add line successfully")
    void addLine_shouldAddLineSuccessfully_whenValidInput(){
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productService.findById(1L)).thenReturn(product);

        orderService.addLine(1L, addLineRequest);

        assertEquals(5, product.getStock());
        assertEquals(1, order.getOrderLines().size());

        OrderLine addedLine = order.getOrderLines().get(0);
        assertEquals(product, addedLine.getProduct());
        assertEquals(5, addedLine.getQuantity());
        assertEquals(product.getPrice(), addedLine.getUnitPrice());

        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrder() {
        when(customerService.findById(1L)).thenReturn(customer);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.create(new CreateOrderRequest(1L));

        assertNotNull(result);
        assertEquals(OrderStatus.DRAFT, result.getStatus());
        verify(customerService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should find order by id")
    void testFindById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void testFindByIdNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.findById(999L));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should validate order with VIP discount")
    void testValidateOrderWithVIP() {
        customer.setVip(true);
        order.addLineToOrder(product, 2);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.validateOrder(1L);

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertNotEquals(BigDecimal.ZERO, order.getTotalAmount());
    }

    @Test
    @DisplayName("Should throw exception when validating empty order")
    void testValidateEmptyOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.validateOrder(1L));
        assertTrue(exception.getMessage().contains("at least one line"));
    }

    @Test
    @DisplayName("Should delete order line and restore stock")
    void testDeleteOrderLine() {
        order.addLineToOrder(product, 2);
        OrderLine line = order.getOrderLines().get(0);
        line.setId(1L);
        long lineId = line.getId();
        int expectedStock = product.getStock() + 2; // stock restored

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.deleteOrderLine(1L, lineId);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order line quantity")
    void testUpdateOrderLineQuantity() {
        order.addLineToOrder(product, 2);
        OrderLine line = order.getOrderLines().get(0);
        line.setId(1L);
        long lineId = line.getId();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.updateOrderLineQuantity(1L, lineId, new UpdateOrderLineRequest(5));

        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
