package com.luqman.rest_api_orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luqman.rest_api_orders.dtos.CreateCustomerRequest;
import com.luqman.rest_api_orders.dtos.CreateProductRequest;
import com.luqman.rest_api_orders.entities.User;
import com.luqman.rest_api_orders.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("OrderController Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;
    private String adminToken;
    private Long customerId;
    private Long productId;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        String adminUser = "oadmin_" + UUID.randomUUID().toString().substring(0, 8);
        String userUser = "ouser_" + UUID.randomUUID().toString().substring(0, 8);
        String pass = "pwd123";

        createUser(adminUser, pass, "ADMIN");
        adminToken = login(adminUser, pass);

        createUser(userUser, pass, "USER");
        token = login(userUser, pass);

        var custReq = new CreateCustomerRequest("Order", "Client", "Address", false);
        String custJson = mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(custReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        customerId = objectMapper.readTree(custJson).get("id").asLong();

        var prodReq = new CreateProductRequest("OrderItem", "desc", BigDecimal.ONE, BigDecimal.TEN, 100);
        String prodJson = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prodReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        productId = objectMapper.readTree(prodJson).get("id").asLong();
    }

    @Test
    @DisplayName("Should create an order as authenticated user")
    void testCreateOrder() throws Exception {
        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":" + customerId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId", is(customerId.intValue())))
                .andExpect(jsonPath("$.status", is("DRAFT")));
    }

    @Test
    @DisplayName("Should list orders as authenticated user")
    void testListOrders() throws Exception {
        mockMvc.perform(get("/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    @DisplayName("Should get order by id")
    void testGetOrderById() throws Exception {
        String json = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":" + customerId + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(get("/orders/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DRAFT")));
    }

    @Test
    @DisplayName("Should add a line to an order")
    void testAddOrderLine() throws Exception {
        String json = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":" + customerId + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(post("/orders/{id}/lines", orderId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + productId + ",\"quantity\":3}"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should validate an order")
    void testValidateOrder() throws Exception {
        String json = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":" + customerId + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(post("/orders/{id}/lines", orderId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + productId + ",\"quantity\":2}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/orders/{id}/validate", orderId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.totalAmount", is(20.0)));
    }

    @Test
    @DisplayName("Should delete an order")
    void testDeleteOrder() throws Exception {
        String json = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":" + customerId + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(delete("/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 422 for invalid order creation")
    void testCreateOrderValidation() throws Exception {
        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity());
    }

    private void createUser(String username, String password, String role) {
        userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build());
    }

    private String login(String username, String password) throws Exception {
        String json = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json).get("token").asText();
    }
}
