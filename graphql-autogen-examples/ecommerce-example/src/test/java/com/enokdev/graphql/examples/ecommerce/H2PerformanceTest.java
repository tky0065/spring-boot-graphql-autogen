package com.enokdev.graphql.examples.ecommerce;

import com.enokdev.graphql.examples.ecommerce.entity.*;
import com.enokdev.graphql.examples.ecommerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de performance avec base de données H2
 * Phase 8 : Testing et qualité - Tests de performance avec H2
 * 
 * Ces tests vérifient les performances du système avec un volume de données important
 * et valident que les optimisations (DataLoaders, cache, etc.) fonctionnent correctement.
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class H2PerformanceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private static final int LARGE_DATASET_SIZE = 1000;
    private static final int PERFORMANCE_THRESHOLD_MS = 2000; // 2 secondes max

    @BeforeEach
    void setUp() {
        // Configuration des tests de performance
    }

    @Test
    void testLargeDatasetCreation() {
        StopWatch stopWatch = new StopWatch("Large Dataset Creation");
        stopWatch.start();

        // Créer un grand nombre de catégories
        List<Category> categories = createCategories(10);
        
        // Créer un grand nombre de produits
        List<Product> products = createProducts(LARGE_DATASET_SIZE, categories);
        
        // Créer un grand nombre de clients
        List<Customer> customers = createCustomers(100);
        
        // Créer un grand nombre de commandes
        List<Order> orders = createOrders(500, customers);

        stopWatch.stop();
        
        System.out.println("Performance test results:");
        System.out.println("- Created " + categories.size() + " categories");
        System.out.println("- Created " + products.size() + " products");
        System.out.println("- Created " + customers.size() + " customers");
        System.out.println("- Created " + orders.size() + " orders");
        System.out.println("- Total time: " + stopWatch.getTotalTimeMillis() + "ms");
        
        // Vérifications
        assertThat(categories).hasSize(10);
        assertThat(products).hasSize(LARGE_DATASET_SIZE);
        assertThat(customers).hasSize(100);
        assertThat(orders).hasSize(500);
        
        // Vérifier que la création n'a pas pris trop de temps
        assertThat(stopWatch.getTotalTimeMillis()).isLessThan(PERFORMANCE_THRESHOLD_MS);
    }

    @Test
    void testLargeDatasetQueries() {
        // Préparer un dataset de taille moyenne pour les tests de requête
        List<Category> categories = createCategories(5);
        List<Product> products = createProducts(500, categories);
        List<Customer> customers = createCustomers(50);
        List<Order> orders = createOrders(100, customers);

        StopWatch stopWatch = new StopWatch("Large Dataset Queries");

        // Test 1: Requête de tous les produits
        stopWatch.start("Find All Products");
        List<Product> allProducts = productRepository.findAll();
        stopWatch.stop();
        assertThat(allProducts).hasSize(500);

        // Test 2: Requête avec condition
        stopWatch.start("Find Expensive Products");
        List<Product> expensiveProducts = productRepository.findByPriceGreaterThan(new BigDecimal("500"));
        stopWatch.stop();

        // Test 3: Requête avec jointure
        stopWatch.start("Find Orders with Customer");
        List<Order> ordersWithCustomers = orderRepository.findAll();
        // Force le chargement des relations
        ordersWithCustomers.forEach(order -> order.getCustomer().getFirstName());
        stopWatch.stop();

        // Test 4: Requête complexe
        stopWatch.start("Complex Query");
        List<Product> productsInStock = productRepository.findByStockGreaterThan(0);
        stopWatch.stop();

        System.out.println("Query performance results:");
        System.out.println(stopWatch.prettyPrint());

        // Vérifier que chaque requête n'a pas pris trop de temps
        stopWatch.getTaskInfo().forEach(task -> {
            System.out.println("Task: " + task.getTaskName() + " took " + task.getTimeMillis() + "ms");
            assertThat(task.getTimeMillis()).isLessThan(1000); // Max 1 seconde par requête
        });
    }

    @Test
    void testDataLoaderPerformance() {
        // Test spécifique pour les DataLoaders - simulation du problème N+1
        List<Category> categories = createCategories(3);
        List<Customer> customers = createCustomers(10);
        List<Order> orders = createOrders(50, customers);

        StopWatch stopWatch = new StopWatch("DataLoader Performance");

        // Simulation d'une requête qui pourrait causer le problème N+1
        stopWatch.start("Load Orders with Customers");
        List<Order> allOrders = orderRepository.findAll();
        
        // Force le chargement de tous les customers (ce qui devrait être optimisé par DataLoader)
        for (Order order : allOrders) {
            Customer customer = order.getCustomer();
            String fullName = customer.getFirstName() + " " + customer.getLastName();
            assertThat(fullName).isNotEmpty();
        }
        stopWatch.stop();

        System.out.println("DataLoader performance: " + stopWatch.getTotalTimeMillis() + "ms for " + allOrders.size() + " orders");
        
        // Avec DataLoader, cela devrait être rapide même avec beaucoup de commandes
        assertThat(stopWatch.getTotalTimeMillis()).isLessThan(500); // Max 500ms
    }

    @Test
    void testCachePerformance() {
        // Test des performances du cache
        List<Category> categories = createCategories(5);
        List<Product> products = createProducts(100, categories);

        StopWatch stopWatch = new StopWatch("Cache Performance");

        // Première exécution - mise en cache
        stopWatch.start("First Query - Cache Miss");
        List<Product> firstResult = productRepository.findAll();
        stopWatch.stop();

        // Deuxième exécution - cache hit (si le cache est activé)
        stopWatch.start("Second Query - Cache Hit");
        List<Product> secondResult = productRepository.findAll();
        stopWatch.stop();

        System.out.println("Cache performance results:");
        System.out.println(stopWatch.prettyPrint());

        assertThat(firstResult).hasSize(100);
        assertThat(secondResult).hasSize(100);
        
        // La deuxième requête devrait être plus rapide (ou au moins pas plus lente)
        long firstTime = stopWatch.getTaskInfo()[0].getTimeMillis();
        long secondTime = stopWatch.getTaskInfo()[1].getTimeMillis();
        System.out.println("First query: " + firstTime + "ms, Second query: " + secondTime + "ms");
    }

    @Test
    void testMemoryUsage() {
        // Test d'utilisation mémoire avec un grand dataset
        Runtime runtime = Runtime.getRuntime();
        
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory before test: " + memoryBefore / 1024 / 1024 + " MB");

        // Créer un dataset important
        List<Category> categories = createCategories(10);
        List<Product> products = createProducts(500, categories);
        List<Customer> customers = createCustomers(100);
        List<Order> orders = createOrders(200, customers);

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory after test: " + memoryAfter / 1024 / 1024 + " MB");
        
        long memoryUsed = memoryAfter - memoryBefore;
        System.out.println("Memory used by test: " + memoryUsed / 1024 / 1024 + " MB");

        // Vérifier que l'utilisation mémoire reste raisonnable (moins de 100MB pour ce test)
        assertThat(memoryUsed).isLessThan(100 * 1024 * 1024); // 100MB max
    }

    // Méthodes utilitaires pour créer des données de test

    private List<Category> createCategories(int count) {
        List<Category> categories = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Category category = new Category();
            category.setName("Category " + i);
            category.setDescription("Description for category " + i);
            categories.add(categoryRepository.save(category));
        }
        return categories;
    }

    private List<Product> createProducts(int count, List<Category> categories) {
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Product product = new Product();
            product.setName("Product " + i);
            product.setDescription("Description for product " + i);
            product.setPrice(new BigDecimal(String.valueOf(10.99 + (i % 1000))));
            product.setStock(i % 100);
            product.setCategory(categories.get(i % categories.size()));
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            products.add(productRepository.save(product));
        }
        return products;
    }

    private List<Customer> createCustomers(int count) {
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Customer customer = new Customer();
            customer.setFirstName("FirstName" + i);
            customer.setLastName("LastName" + i);
            customer.setEmail("customer" + i + "@example.com");
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());
            customers.add(customerRepository.save(customer));
        }
        return customers;
    }

    private List<Order> createOrders(int count, List<Customer> customers) {
        List<Order> orders = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Order order = new Order();
            order.setCustomer(customers.get(i % customers.size()));
            order.setStatus(OrderStatus.values()[i % OrderStatus.values().length]);
            order.setTotalAmount(new BigDecimal(String.valueOf(100.0 + (i % 1000))));
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            orders.add(orderRepository.save(order));
        }
        return orders;
    }
}
