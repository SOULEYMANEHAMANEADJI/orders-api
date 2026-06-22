package com.luqman.rest_api_orders.controllers;

import com.luqman.rest_api_orders.dtos.AddOrderLineRequest;
import com.luqman.rest_api_orders.dtos.CreateOrderRequest;
import com.luqman.rest_api_orders.dtos.OrderResponse;
import com.luqman.rest_api_orders.dtos.OrderLineResponse;
import com.luqman.rest_api_orders.dtos.UpdateOrderLineRequest;
import com.luqman.rest_api_orders.dtos.UpdateOrderRequest;
import com.luqman.rest_api_orders.entities.Order;
import com.luqman.rest_api_orders.services.OrderService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/orders")
@RestController
@Tag(name = "Orders", description = "Order management")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "List all orders (paginated)")
    public ResponseEntity<Page<OrderResponse>> listOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(pageable).map(this::toResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order by id")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(orderService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Create an order")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        Order order = orderService.create(createOrderRequest);
        return ResponseEntity.status(201).body(toResponse(order));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an order")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id, @Valid @RequestBody UpdateOrderRequest request) {
        Order order = orderService.update(id, request);
        return ResponseEntity.ok(toResponse(order));
    }

    @PostMapping("{id}/lines")
    @Operation(summary = "Add a line to an order")
    public ResponseEntity<Void> addOrderLine(@PathVariable Long id, @Valid @RequestBody AddOrderLineRequest addOrderLineRequest) {
        orderService.addLine(id, addOrderLineRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/validate")
    @Operation(summary = "Validate an order")
    public ResponseEntity<Void> validateOrder(@PathVariable Long id) {
        orderService.validateOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/lines")
    @Operation(summary = "List order lines")
    public ResponseEntity<List<OrderLineResponse>> getOrderLines(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderLines(id));
    }

    @DeleteMapping("/{id}/lines/{lineId}")
    @Operation(summary = "Delete an order line")
    public ResponseEntity<Void> deleteOrderLine(@PathVariable Long id, @PathVariable Long lineId) {
        orderService.deleteOrderLine(id, lineId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/lines/{lineId}")
    @Operation(summary = "Update order line quantity")
    public ResponseEntity<Void> updateOrderLine(@PathVariable Long id, @PathVariable Long lineId, @Valid @RequestBody UpdateOrderLineRequest request) {
        orderService.updateOrderLineQuantity(id, lineId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomer() != null ? order.getCustomer().getId() : null,
                order.getStatus(),
                order.getTotalAmount(),
                order.getOrderLines().stream()
                        .map(line -> new OrderLineResponse(
                                line.getId(),
                                line.getProduct().getId(),
                                line.getProduct().getName(),
                                line.getQuantity(),
                                line.getUnitPrice(),
                                line.getLineTotal()
                        ))
                        .toList()
        );
    }
}
