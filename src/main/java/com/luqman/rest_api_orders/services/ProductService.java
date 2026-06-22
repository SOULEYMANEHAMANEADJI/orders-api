package com.luqman.rest_api_orders.services;

import com.luqman.rest_api_orders.dtos.CreateProductRequest;
import com.luqman.rest_api_orders.dtos.UpdateProductRequest;
import com.luqman.rest_api_orders.entities.Product;
import com.luqman.rest_api_orders.exceptions.BusinessException;
import com.luqman.rest_api_orders.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product with id " + id + " not found"));
    }

    @Transactional
    public Product create(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setWeight(request.weight());
        product.setPrice(request.price());
        product.setStock(request.stock());
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, UpdateProductRequest request) {
        Product product = findById(id);

        if (request.name() != null) {
            product.setName(request.name());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.weight() != null) {
            product.setWeight(request.weight());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.stock() != null) {
            product.setStock(request.stock());
        }

        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException("Product with id " + id + " not found");
        }
        productRepository.deleteById(id);
    }
}
