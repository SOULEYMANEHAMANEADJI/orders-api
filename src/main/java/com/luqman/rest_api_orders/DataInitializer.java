package com.luqman.rest_api_orders;

import com.luqman.rest_api_orders.entities.Customer;
import com.luqman.rest_api_orders.entities.Product;
import com.luqman.rest_api_orders.repositories.ProductRepository;
import com.luqman.rest_api_orders.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        seedCustomers();
        seedProducts();
    }

    private void seedCustomers() {
        if (customerRepository.count() > 0) {
            return;
        }

        Customer c1 = new Customer();
        c1.setFirstName("Luqman");
        c1.setLastName("Diallo");
        c1.setAddress("Abidjan");
        c1.setVip(true);

        Customer c2 = new Customer();
        c2.setFirstName("Awa");
        c2.setLastName("Traore");
        c2.setAddress("Bouake");
        c2.setVip(false);

        customerRepository.save(c1);
        customerRepository.save(c2);
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        Product p1 = new Product();
        p1.setName("Laptop Pro 14");
        p1.setDescription("Ordinateur portable pour usage bureautique et dev");
        p1.setWeight(new BigDecimal("1.450"));
        p1.setPrice(new BigDecimal("850000.00"));
        p1.setStock(10);

        Product p2 = new Product();
        p2.setName("Souris sans fil");
        p2.setDescription("Souris ergonomique Bluetooth");
        p2.setWeight(new BigDecimal("0.120"));
        p2.setPrice(new BigDecimal("25000.00"));
        p2.setStock(50);

        Product p3 = new Product();
        p3.setName("Clavier mecanique");
        p3.setDescription("Clavier AZERTY mecanique RGB");
        p3.setWeight(new BigDecimal("0.950"));
        p3.setPrice(new BigDecimal("70000.00"));
        p3.setStock(25);

        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);
    }
}

