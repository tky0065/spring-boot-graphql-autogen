package com.enokdev.graphql.examples.ecommerce;

import com.enokdev.graphql.examples.ecommerce.entity.*;
import com.enokdev.graphql.examples.ecommerce.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests d'intégration GraphQL avec base de données H2
 * Phase 8 : Testing et qualité - Tests avec base de données H2 réelle
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class GraphQLH2IntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Customer customer;
    private Product product;
    private Category category;
    private Order order;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        setupTestData();
    }

    private void setupTestData() {
        category = new Category();
        category.setName("Electronics");
        category.setDescription("Electronic products and gadgets");
        category = categoryRepository.save(category);

        product = new Product();
        product.setName("Gaming Laptop");
        product.setDescription("High-performance gaming laptop with RTX graphics");
        product.setPrice(new BigDecimal("1299.99"));
        product.setStock(5);
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product = productRepository.save(product);

        customer = new Customer();
        customer.setFirstName("Alice");
        customer.setLastName("Johnson");
        customer.setEmail("alice.johnson@example.com");
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer = customerRepository.save(customer);

        order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("1299.99"));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);
    }

    @Test
    void testGraphQLQueryProducts() throws Exception {
        String query = """
            {
                products {
                    id
                    name
                    description
                    price
                    stock
                    category {
                        id
                        name
                        description
                    }
                }
            }
            """;

        Map<String, Object> request = new HashMap<>();
        request.put("query", query);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.products", hasSize(1)))
                .andExpect(jsonPath("$.data.products[0].name", is("Gaming Laptop")))
                .andExpect(jsonPath("$.data.products[0].price", is(1299.99)));
    }

    @Test
    void testGraphQLQueryCustomers() throws Exception {
        String query = """
            {
                customers {
                    id
                    firstName
                    lastName
                    email
                    createdAt
                }
            }
            """;

        Map<String, Object> request = new HashMap<>();
        request.put("query", query);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customers", hasSize(1)))
                .andExpect(jsonPath("$.data.customers[0].firstName", is("Alice")))
                .andExpect(jsonPath("$.data.customers[0].email", is("alice.johnson@example.com")));
    }

    @Test
    void testGraphQLMutationCreateProduct() throws Exception {
        String mutation = """
            mutation {
                createProduct(input: {
                    name: "Wireless Mouse"
                    description: "Ergonomic wireless mouse"
                    price: 29.99
                    stock: 50
                    categoryId: %d
                }) {
                    id
                    name
                    description
                    price
                    stock
                }
            }
            """.formatted(category.getId());

        Map<String, Object> request = new HashMap<>();
        request.put("query", mutation);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createProduct.name", is("Wireless Mouse")))
                .andExpect(jsonPath("$.data.createProduct.price", is(29.99)));
    }
}
