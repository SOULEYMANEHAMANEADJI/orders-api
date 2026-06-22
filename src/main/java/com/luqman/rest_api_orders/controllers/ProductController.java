package com.luqman.rest_api_orders.controllers;

import com.luqman.rest_api_orders.dtos.CreateProductRequest;
import com.luqman.rest_api_orders.dtos.ProductResponse;
import com.luqman.rest_api_orders.dtos.UpdateProductRequest;
import com.luqman.rest_api_orders.entities.Product;
import com.luqman.rest_api_orders.services.ProductService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Products", description = "Operations on products")
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List products (paginated)")
    public ResponseEntity<Page<ProductResponse>> getProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable).map(this::toResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by id")
    public ResponseEntity<ProductResponse> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(productService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.create(request);
        return ResponseEntity.status(201).body(toResponse(product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        Product product = productService.update(id, request);
        return ResponseEntity.ok(toResponse(product));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getWeight(),
                product.getPrice(),
                product.getStock()
        );
    }
}
