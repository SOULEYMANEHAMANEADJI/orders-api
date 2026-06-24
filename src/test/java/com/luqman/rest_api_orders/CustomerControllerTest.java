package com.luqman.rest_api_orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luqman.rest_api_orders.dtos.CreateCustomerRequest;
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

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CustomerController Tests")
class CustomerControllerTest {

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
        String adminUser = "cadmin_" + UUID.randomUUID().toString().substring(0, 8);
        String userUser = "cuser_" + UUID.randomUUID().toString().substring(0, 8);
        String pass = "pwd123";

        createUser(adminUser, pass, "ADMIN");
        adminToken = login(adminUser, pass);

        createUser(userUser, pass, "USER");
        userToken = login(userUser, pass);
    }

    @Test
    @DisplayName("Should create a customer as admin")
    void testCreateCustomer() throws Exception {
        var request = new CreateCustomerRequest("John", "Doe", "123 Street", true);

        mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.vip", is(true)));
    }

    @Test
    @DisplayName("Should reject create customer as non-admin")
    void testCreateCustomerForbiddenForUser() throws Exception {
        var request = new CreateCustomerRequest("Jane", "Doe", "456 Avenue", false);

        mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should list customers as authenticated user")
    void testListCustomers() throws Exception {
        mockMvc.perform(get("/customers")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    @DisplayName("Should get customer by id as authenticated user")
    void testGetCustomerById() throws Exception {
        var request = new CreateCustomerRequest("Find", "Me", "789 Road", false);
        String json = mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(get("/customers/{id}", id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Find")));
    }

    @Test
    @DisplayName("Should update a customer as admin")
    void testUpdateCustomer() throws Exception {
        var createReq = new CreateCustomerRequest("Old", "Name", "Addr", false);
        String json = mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(json).get("id").asLong();

        String updateBody = "{\"firstName\":\"New\",\"vip\":true}";
        mockMvc.perform(put("/customers/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("New")))
                .andExpect(jsonPath("$.vip", is(true)));
    }

    @Test
    @DisplayName("Should delete a customer as admin")
    void testDeleteCustomer() throws Exception {
        var request = new CreateCustomerRequest("Del", "Ete", "Nowhere", false);
        String json = mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(delete("/customers/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 422 for invalid customer creation")
    void testCreateCustomerValidation() throws Exception {
        String invalidBody = "{\"firstName\":\"\",\"lastName\":\"\",\"address\":\"\"}";
        mockMvc.perform(post("/customers")
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
