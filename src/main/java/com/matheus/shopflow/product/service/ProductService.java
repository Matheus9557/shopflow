package com.matheus.shopflow.product.service;

import com.matheus.shopflow.product.dto.CachedProductResponse;
import com.matheus.shopflow.product.dto.ProductRequest;
import com.matheus.shopflow.product.dto.ProductResponse;
import com.matheus.shopflow.product.entity.Product;
import com.matheus.shopflow.product.repository.ProductRepository;
import com.matheus.shopflow.shared.exception.NotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ProductService {

    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final ProductRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    public ProductService(
            ProductRepository repository,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    public ProductResponse create(ProductRequest request) {

        Product product = new Product(
                request.name(),
                request.description(),
                request.price()
        );

        Product saved = repository.save(product);

        return toResponse(saved);
    }

    public ProductResponse getById(Long id) {

        String cacheKey = PRODUCT_CACHE_PREFIX + id;

        Object cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached instanceof CachedProductResponse productCache) {
            System.out.println("REDIS HIT");

            return new ProductResponse(
                    productCache.id(),
                    productCache.name(),
                    productCache.description(),
                    productCache.price(),
                    null
            );
        }

        System.out.println("REDIS MISS");

        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        CachedProductResponse cacheValue =
                new CachedProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice()
                );

        redisTemplate.opsForValue().set(
                cacheKey,
                cacheValue,
                CACHE_TTL
        );

        return toResponse(product);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCreatedAt()
        );
    }
}