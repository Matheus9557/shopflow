package com.matheus.shopflow.product.service;

import com.matheus.shopflow.product.dto.CachedProductResponse;
import com.matheus.shopflow.product.dto.ProductRequest;
import com.matheus.shopflow.product.dto.ProductResponse;
import com.matheus.shopflow.product.entity.Product;
import com.matheus.shopflow.product.repository.ProductRepository;
import com.matheus.shopflow.shared.exception.NotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class ProductService {

    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final ProductRepository repository;
    private final RedisTemplate<String, CachedProductResponse> redisTemplate;

    public ProductService(
            ProductRepository repository,
            RedisTemplate<String, CachedProductResponse> redisTemplate
    ) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
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

        String cacheKey = buildCacheKey(id);

        CachedProductResponse cached =
                redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            System.out.println("REDIS HIT");

            return new ProductResponse(
                    cached.id(),
                    cached.name(),
                    cached.description(),
                    cached.price(),
                    cached.createdAt()
            );
        }

        System.out.println("REDIS MISS");

        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        CachedProductResponse cacheValue =
                toCachedResponse(product);

        redisTemplate.opsForValue().set(
                cacheKey,
                cacheValue,
                CACHE_TTL
        );

        return toResponse(product);
    }

    @Transactional
    public ProductResponse update(
            Long id,
            ProductRequest request
    ) {

        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        product.changeName(request.name());
        product.changeDescription(request.description());
        product.changePrice(request.price());

        Product updated = repository.save(product);

        updateCache(updated);

        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        repository.delete(product);

        evictCache(id);
    }

    private void updateCache(Product product) {

        String cacheKey = buildCacheKey(product.getId());

        CachedProductResponse cacheValue =
                toCachedResponse(product);

        redisTemplate.opsForValue().set(
                cacheKey,
                cacheValue,
                CACHE_TTL
        );
    }

    private void evictCache(Long id) {

        String cacheKey = buildCacheKey(id);

        redisTemplate.delete(cacheKey);
    }

    private String buildCacheKey(Long id) {
        return PRODUCT_CACHE_PREFIX + id;
    }

    private CachedProductResponse toCachedResponse(Product product) {

        return new CachedProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCreatedAt()
        );
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