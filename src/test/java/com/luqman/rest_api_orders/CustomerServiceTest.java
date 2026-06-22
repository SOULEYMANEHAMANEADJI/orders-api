package com.luqman.rest_api_orders;

import com.luqman.rest_api_orders.dtos.CreateCustomerRequest;
import com.luqman.rest_api_orders.dtos.UpdateCustomerRequest;
import com.luqman.rest_api_orders.entities.Customer;
import com.luqman.rest_api_orders.exceptions.BusinessException;
import com.luqman.rest_api_orders.repositories.CustomerRepository;
import com.luqman.rest_api_orders.services.CustomerService;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CreateCustomerRequest createRequest;
    private UpdateCustomerRequest updateRequest;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setAddress("123 Main St");
        customer.setVip(false);

        createRequest = new CreateCustomerRequest("John", "Doe", "123 Main St", false);
        updateRequest = new UpdateCustomerRequest("Jane", "Doe", "456 Oak St", true);
    }

    @Test
    @DisplayName("Should create a customer successfully")
    void testCreateCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.create(createRequest);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should find customer by id")
    void testFindById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void testFindByIdNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> customerService.findById(999L));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should find all customers with pagination")
    void testFindAllPaginated() {
        Page<Customer> page = new PageImpl<>(List.of(customer), PageRequest.of(0, 10), 1);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Customer> result = customerService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Should find all customers without pagination")
    void testFindAll() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<Customer> result = customerService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should update customer successfully")
    void testUpdateCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.update(1L, updateRequest);

        assertNotNull(result);
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should update only provided fields")
    void testUpdatePartialCustomer() {
        UpdateCustomerRequest partialUpdate = new UpdateCustomerRequest("Jane", null, null, null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        customerService.update(1L, partialUpdate);

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent customer")
    void testUpdateCustomerNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> customerService.update(999L, updateRequest));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void testDeleteCustomer() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        customerService.delete(1L);

        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent customer")
    void testDeleteCustomerNotFound() {
        when(customerRepository.existsById(999L)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> customerService.delete(999L));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should handle VIP status correctly")
    void testVipCustomer() {
        Customer vipCustomer = new Customer();
        vipCustomer.setId(2L);
        vipCustomer.setFirstName("VIP");
        vipCustomer.setLastName("Customer");
        vipCustomer.setVip(true);

        when(customerRepository.save(any(Customer.class))).thenReturn(vipCustomer);

        Customer result = customerService.create(
                new CreateCustomerRequest("VIP", "Customer", "Address", true)
        );

        assertTrue(result.isVip());
    }
}




