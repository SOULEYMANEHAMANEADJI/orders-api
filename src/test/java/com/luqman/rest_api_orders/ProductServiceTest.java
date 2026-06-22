package com.luqman.rest_api_orders;

import com.luqman.rest_api_orders.dtos.CreateProductRequest;
import com.luqman.rest_api_orders.dtos.UpdateProductRequest;
import com.luqman.rest_api_orders.entities.Product;
import com.luqman.rest_api_orders.exceptions.BusinessException;
import com.luqman.rest_api_orders.repositories.ProductRepository;
import com.luqman.rest_api_orders.services.ProductService;
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
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private CreateProductRequest createRequest;
    private UpdateProductRequest updateRequest;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("High-performance laptop");
        product.setWeight(new BigDecimal("1.50"));
        product.setPrice(new BigDecimal("999.99"));
        product.setStock(10);

        createRequest = new CreateProductRequest(
                "Laptop",
                "High-performance laptop",
                new BigDecimal("1.50"),
                new BigDecimal("999.99"),
                10
        );

        updateRequest = new UpdateProductRequest(
                "Laptop Pro",
                "Updated laptop",
                new BigDecimal("1.60"),
                new BigDecimal("1199.99"),
                15
        );
    }

    @Test
    @DisplayName("Should create a product successfully")
    void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.create(createRequest);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(new BigDecimal("999.99"), result.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should find product by id")
    void testFindById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void testFindByIdNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.findById(999L));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should find all products with pagination")
    void testFindAllPaginated() {
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Laptop", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should find all products without pagination")
    void testFindAll() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.update(1L, updateRequest);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void testUpdateProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.update(999L, updateRequest));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.delete(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void testDeleteProductNotFound() {
        when(productRepository.existsById(999L)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.delete(999L));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should reserve stock successfully")
    void testReserveStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        int initialStock = product.getStock();
        productService.findById(1L).reserveStock(5);

        assertEquals(initialStock - 5, product.getStock());
    }

    @Test
    @DisplayName("Should throw exception when reserving more stock than available")
    void testReserveStockInsufficientQuantity() {
        Product productLowStock = new Product();
        productLowStock.setStock(2);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> productLowStock.reserveStock(5));
        assertTrue(exception.getMessage().contains("Not enough stock"));
    }
}




