package com.luqman.rest_api_orders.services;

import com.luqman.rest_api_orders.dtos.AddOrderLineRequest;
import com.luqman.rest_api_orders.dtos.CreateOrderRequest;
import com.luqman.rest_api_orders.dtos.OrderLineResponse;
import com.luqman.rest_api_orders.dtos.UpdateOrderLineRequest;
import com.luqman.rest_api_orders.dtos.UpdateOrderRequest;
import com.luqman.rest_api_orders.entities.Customer;
import com.luqman.rest_api_orders.entities.Order;
import com.luqman.rest_api_orders.entities.OrderLine;
import com.luqman.rest_api_orders.entities.Product;
import com.luqman.rest_api_orders.exceptions.BusinessException;
import com.luqman.rest_api_orders.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    @Transactional
    public Order create(CreateOrderRequest createOrderRequest) {
        Order order = new Order();
        Customer customer = customerService.findById(createOrderRequest.customerId());
        order.setCustomer(customer);
        return orderRepository.save(order);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Order with id " + id + " not found"));
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order update(Long id, UpdateOrderRequest request) {
        Order order = findById(id);
        if (request.customerId() != null) {
            order.setCustomer(customerService.findById(request.customerId()));
        }
        return orderRepository.save(order);
    }

    @Transactional
    public void addLine(Long orderId, AddOrderLineRequest addOrderLineRequest) {
        Order order = findById(orderId);
        Product product = productService.findById(addOrderLineRequest.productId());
        order.addLineToOrder(product, addOrderLineRequest.quantity());
        orderRepository.save(order);
    }

    @Transactional
    public void validateOrder(Long orderId) {
        Order order = this.findById(orderId);
        order.validateOrder();
        orderRepository.save(order);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new BusinessException("Order with id " + id + " not found");
        }
        orderRepository.deleteById(id);
    }

    public List<OrderLineResponse> getOrderLines(Long orderId) {
        Order order = findById(orderId);
        return order.getOrderLines().stream()
                .map(this::toOrderLineResponse)
                .toList();
    }

    @Transactional
    public void deleteOrderLine(Long orderId, Long lineId) {
        Order order = findById(orderId);
        OrderLine line = order.getOrderLines().stream()
                .filter(l -> l.getId().equals(lineId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Order line not found"));

        // Restore stock when deleting a line
        line.getProduct().setStock(line.getProduct().getStock() + line.getQuantity());

        order.getOrderLines().remove(line);
        orderRepository.save(order);
    }

    @Transactional
    public void updateOrderLineQuantity(Long orderId, Long lineId, UpdateOrderLineRequest request) {
        Order order = findById(orderId);
        OrderLine line = order.getOrderLines().stream()
                .filter(l -> l.getId().equals(lineId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Order line not found"));

        int quantityDifference = request.quantity() - line.getQuantity();

        // Update stock
        Product product = line.getProduct();
        if (quantityDifference > 0) {
            product.reserveStock(quantityDifference);
        } else if (quantityDifference < 0) {
            product.setStock(product.getStock() - quantityDifference);
        }

        line.setQuantity(request.quantity());
        orderRepository.save(order);
    }

    private OrderLineResponse toOrderLineResponse(OrderLine line) {
        return new OrderLineResponse(
                line.getId(),
                line.getProduct().getId(),
                line.getProduct().getName(),
                line.getQuantity(),
                line.getUnitPrice(),
                line.getLineTotal()
        );
    }
}
