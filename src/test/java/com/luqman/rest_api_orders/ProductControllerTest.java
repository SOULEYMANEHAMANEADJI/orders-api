package com.luqman.rest_api_orders;

import com.fasterxml.jackson.databind.ObjectMapper;
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
@DisplayName("ProductController Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        String adminUser = "padmin_" + UUID.randomUUID().toString().substring(0, 8);
        String userUser = "puser_" + UUID.randomUUID().toString().substring(0, 8);
        String pass = "pwd123";

        createUser(adminUser, pass, "ADMIN");
        adminToken = login(adminUser, pass);

        createUser(userUser, pass, "USER");
        userToken = login(userUser, pass);
    }

    @Test
    @DisplayName("Should create a product as admin")
    void testCreateProduct() throws Exception {
        var request = new CreateProductRequest("Test Product", "desc", BigDecimal.valueOf(1.5), BigDecimal.valueOf(99.99), 10);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andExpect(jsonPath("$.stock", is(10)));
    }

    @Test
    @DisplayName("Should reject create product as non-admin")
    void testCreateProductForbiddenForUser() throws Exception {
        var request = new CreateProductRequest("Test Product", "desc", BigDecimal.valueOf(1.5), BigDecimal.valueOf(99.99), 10);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should list products as authenticated user")
    void testListProducts() throws Exception {
        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    @DisplayName("Should get product by id as authenticated user")
    void testGetProductById() throws Exception {
        var request = new CreateProductRequest("GetTest", "desc", BigDecimal.ONE, BigDecimal.TEN, 5);
        String json = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(get("/products/{id}", id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("GetTest")));
    }

    @Test
    @DisplayName("Should update a product as admin")
    void testUpdateProduct() throws Exception {
        var createReq = new CreateProductRequest("Before", "desc", BigDecimal.ONE, BigDecimal.TEN, 5);
        String json = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(json).get("id").asLong();

        String updateBody = "{\"name\":\"After\",\"price\":20.00}";
        mockMvc.perform(put("/products/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("After")))
                .andExpect(jsonPath("$.price", is(20.00)));
    }

    @Test
    @DisplayName("Should delete a product as admin")
    void testDeleteProduct() throws Exception {
        var request = new CreateProductRequest("ToDelete", "desc", BigDecimal.ONE, BigDecimal.TEN, 5);
        String json = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(delete("/products/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 422 for invalid product creation")
    void testCreateProductValidation() throws Exception {
        String invalidBody = "{\"name\":\"\",\"weight\":-1,\"price\":-1,\"stock\":-1}";
        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
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
