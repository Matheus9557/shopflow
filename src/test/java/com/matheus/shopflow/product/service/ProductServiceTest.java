package com.matheus.shopflow.product.service;

import com.matheus.shopflow.product.dto.CachedProductResponse;
import com.matheus.shopflow.product.dto.ProductRequest;
import com.matheus.shopflow.product.dto.ProductResponse;
import com.matheus.shopflow.product.entity.Product;
import com.matheus.shopflow.product.repository.ProductRepository;
import com.matheus.shopflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private RedisTemplate<String, CachedProductResponse> redisTemplate;

    @Mock
    private ValueOperations<String, CachedProductResponse> valueOperations;

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(repository, redisTemplate);
    }

    @Test
    void shouldCreateProduct() {

        ProductRequest request = new ProductRequest(
                "Notebook Gamer",
                "Notebook para testes",
                new BigDecimal("4999.90"),
                10
        );

        Product product = new Product(
                request.name(),
                request.description(),
                request.price()
        );

        when(repository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("Notebook Gamer", response.name());
        assertEquals("Notebook para testes", response.description());
        assertEquals(
                new BigDecimal("4999.90"),
                response.price()
        );

        verify(repository).save(any(Product.class));
    }

    @Test
    void shouldReturnProductFromDatabaseAndPopulateCacheOnCacheMiss() {

        Long id = 1L;

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        Product product = new Product(
                "Notebook Gamer",
                "Notebook para testes",
                new BigDecimal("4999.90")
        );

        when(valueOperations.get("product:1"))
                .thenReturn(null);

        when(repository.findById(id))
                .thenReturn(Optional.of(product));

        ProductResponse response = service.getById(id);

        assertNotNull(response);
        assertEquals("Notebook Gamer", response.name());
        assertEquals(
                new BigDecimal("4999.90"),
                response.price()
        );

        verify(valueOperations).get("product:1");

        verify(repository).findById(id);

        verify(valueOperations).set(
                eq("product:1"),
                any(CachedProductResponse.class),
                eq(Duration.ofMinutes(10))
        );
    }

    @Test
    void shouldReturnProductFromCacheOnCacheHit() {

        Long id = 1L;

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 9, 8, 22, 34, 32);

        CachedProductResponse cached =
                new CachedProductResponse(
                        id,
                        "Notebook Gamer",
                        "Notebook para testes",
                        new BigDecimal("4999.90"),
                        createdAt
                );

        when(valueOperations.get("product:1"))
                .thenReturn(cached);

        ProductResponse response = service.getById(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Notebook Gamer", response.name());
        assertEquals(
                new BigDecimal("4999.90"),
                response.price()
        );
        assertEquals(createdAt, response.createdAt());

        verify(valueOperations).get("product:1");

        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenProductDoesNotExist() {

        Long id = 999L;

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get("product:999"))
                .thenReturn(null);

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.getById(id)
        );

        verify(valueOperations).get("product:999");

        verify(repository).findById(id);

        verify(valueOperations, never())
                .set(
                        anyString(),
                        any(CachedProductResponse.class),
                        any(Duration.class)
                );
    }

    @Test
    void shouldUpdateProductAndCache() {

        Long id = 1L;

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        Product product = spy(new Product(
                "Notebook Gamer",
                "Notebook antigo",
                new BigDecimal("4999.90")
        ));

        doReturn(id).when(product).getId();

        ProductRequest request = new ProductRequest(
                "Notebook Gamer Pro",
                "Notebook atualizado",
                new BigDecimal("5499.90"),
                10
        );

        when(repository.findById(id))
                .thenReturn(Optional.of(product));

        when(repository.save(product))
                .thenReturn(product);

        ProductResponse response =
                service.update(id, request);

        assertEquals("Notebook Gamer Pro", response.name());
        assertEquals("Notebook atualizado", response.description());
        assertEquals(
                new BigDecimal("5499.90"),
                response.price()
        );

        verify(repository).findById(id);
        verify(repository).save(product);

        verify(valueOperations).set(
                eq("product:1"),
                any(CachedProductResponse.class),
                eq(Duration.ofMinutes(10))
        );
    }

    @Test
    void shouldDeleteProductAndEvictCache() {

        Long id = 1L;

        Product product = new Product(
                "Notebook Gamer",
                "Notebook para testes",
                new BigDecimal("4999.90")
        );

        when(repository.findById(id))
                .thenReturn(Optional.of(product));

        service.delete(id);

        verify(repository).findById(id);
        verify(repository).delete(product);

        verify(redisTemplate).delete("product:1");
    }

    @Test
    void shouldNotDeleteProductWhenProductDoesNotExist() {

        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.delete(id)
        );

        verify(repository).findById(id);
        verify(repository, never()).delete(any(Product.class));
        verify(redisTemplate, never()).delete(anyString());
    }
}