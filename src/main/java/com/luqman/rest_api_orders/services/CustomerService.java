package com.luqman.rest_api_orders.services;

import com.luqman.rest_api_orders.dtos.CreateCustomerRequest;
import com.luqman.rest_api_orders.dtos.UpdateCustomerRequest;
import com.luqman.rest_api_orders.entities.Customer;
import com.luqman.rest_api_orders.exceptions.BusinessException;
import com.luqman.rest_api_orders.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Customer with id " + id + " not found"));
    }

    @Transactional
    public Customer create(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setAddress(request.address());
        customer.setVip(request.vip());
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer update(Long id, UpdateCustomerRequest request) {
        Customer customer = findById(id);

        if (request.firstName() != null) {
            customer.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            customer.setLastName(request.lastName());
        }
        if (request.address() != null) {
            customer.setAddress(request.address());
        }
        if (request.vip() != null) {
            customer.setVip(request.vip());
        }

        return customerRepository.save(customer);
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new BusinessException("Customer with id " + id + " not found");
        }
        customerRepository.deleteById(id);
    }
}
