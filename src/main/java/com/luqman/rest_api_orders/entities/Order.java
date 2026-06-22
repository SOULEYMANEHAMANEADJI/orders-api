package com.luqman.rest_api_orders.entities;

import com.luqman.rest_api_orders.exceptions.BusinessException;
import com.luqman.rest_api_orders.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders_tbl")
public class Order {

    private static final BigDecimal VIP_DISCOUNT_RATE = new BigDecimal("0.90");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.DRAFT;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Version
    private Long version;

    public void addLineToOrder(Product product, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("Quantity must be greater than 0");
        }
        product.reserveStock(quantity);

        OrderLine orderLine = new OrderLine();
        orderLine.setOrder(this);
        orderLine.setProduct(product);
        orderLine.setQuantity(quantity);
        orderLine.setUnitPrice(product.getPrice());
        this.orderLines.add(orderLine);
    }

    public void validateOrder() {
        if (orderLines == null || orderLines.isEmpty()) {
            throw new BusinessException("Order must contain at least one line");
        }

        BigDecimal calculatedTotal = BigDecimal.ZERO;
        for (OrderLine orderLine : orderLines) {
            calculatedTotal = calculatedTotal.add(orderLine.getLineTotal());
        }

        if (this.customer != null && this.customer.isVip()) {
            calculatedTotal = calculatedTotal.multiply(VIP_DISCOUNT_RATE);
        }

        this.totalAmount = calculatedTotal;
        this.status = OrderStatus.CONFIRMED;
    }
}

