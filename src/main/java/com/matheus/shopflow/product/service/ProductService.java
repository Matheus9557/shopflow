package com.matheus.shopflow.product.service;

import com.matheus.shopflow.product.dto.CachedProductResponse;
import com.matheus.shopflow.product.dto.ProductPageResponse;
import com.matheus.shopflow.product.dto.ProductRequest;
import com.matheus.shopflow.product.dto.ProductResponse;
import com.matheus.shopflow.product.entity.Product;
import com.matheus.shopflow.product.repository.ProductRepository;
import com.matheus.shopflow.shared.exception.BusinessException;
import com.matheus.shopflow.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class ProductService {

    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private static final int MAX_PAGE_SIZE = 50;

    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION =
            Sort.Direction.DESC;

    private static final String[] ALLOWED_SORT_FIELDS = {
            "name",
            "price",
            "createdAt"
    };

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
                .orElseThrow(() ->
                        new NotFoundException("Product not found")
                );

        CachedProductResponse cacheValue =
                toCachedResponse(product);

        redisTemplate.opsForValue().set(
                cacheKey,
                cacheValue,
                CACHE_TTL
        );

        return toResponse(product);
    }

    public ProductPageResponse findAll(
            int page,
            int size,
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sort
    ) {

        validatePagination(page, size);

        Sort sorting = buildSort(sort);

        Pageable pageable = PageRequest.of(
                page,
                size,
                sorting
        );

        Specification<Product> specification =
                buildSpecification(
                        name,
                        minPrice,
                        maxPrice
                );

        Page<ProductResponse> result =
                repository.findAll(
                        specification,
                        pageable
                ).map(this::toResponse);

        return ProductPageResponse.from(result);
    }

    @Transactional
    public ProductResponse update(
            Long id,
            ProductRequest request
    ) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Product not found")
                );

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
                .orElseThrow(() ->
                        new NotFoundException("Product not found")
                );

        repository.delete(product);

        evictCache(id);
    }

    private Specification<Product> buildSpecification(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {

        Specification<Product> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        if (name != null && !name.isBlank()) {

            String normalizedName =
                    "%" + name.trim().toLowerCase() + "%";

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(
                                            root.get("name")
                                    ),
                                    normalizedName
                            )
            );
        }

        if (minPrice != null) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("price"),
                                    minPrice
                            )
            );
        }

        if (maxPrice != null) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("price"),
                                    maxPrice
                            )
            );
        }

        return specification;
    }

    private Sort buildSort(String sort) {

        if (sort == null || sort.isBlank()) {
            return Sort.by(
                    DEFAULT_SORT_DIRECTION,
                    DEFAULT_SORT_FIELD
            );
        }

        String[] parts = sort.split(",");

        if (parts.length > 2) {
            throw new BusinessException(
                    "Invalid sort format. Use field,direction"
            );
        }

        String field = parts[0].trim();

        if (!isAllowedSortField(field)) {
            throw new BusinessException(
                    "Invalid sort field. Allowed fields: name, price, createdAt"
            );
        }

        Sort.Direction direction =
                DEFAULT_SORT_DIRECTION;

        if (parts.length == 2) {

            String directionValue =
                    parts[1].trim().toLowerCase();

            try {
                direction =
                        Sort.Direction.fromString(
                                directionValue
                        );
            } catch (IllegalArgumentException ex) {
                throw new BusinessException(
                        "Invalid sort direction. Use asc or desc"
                );
            }
        }

        return Sort.by(direction, field);
    }

    private boolean isAllowedSortField(String field) {

        for (String allowedField : ALLOWED_SORT_FIELDS) {

            if (allowedField.equals(field)) {
                return true;
            }
        }

        return false;
    }

    private void validatePagination(
            int page,
            int size
    ) {

        if (page < 0) {
            throw new BusinessException(
                    "Page must be greater than or equal to zero"
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new BusinessException(
                    "Page size must be between 1 and "
                            + MAX_PAGE_SIZE
            );
        }
    }

    private void updateCache(Product product) {

        String cacheKey =
                buildCacheKey(product.getId());

        CachedProductResponse cacheValue =
                toCachedResponse(product);

        redisTemplate.opsForValue().set(
                cacheKey,
                cacheValue,
                CACHE_TTL
        );
    }

    private void evictCache(Long id) {

        String cacheKey =
                buildCacheKey(id);

        redisTemplate.delete(cacheKey);
    }

    private String buildCacheKey(Long id) {
        return PRODUCT_CACHE_PREFIX + id;
    }

    private CachedProductResponse toCachedResponse(
            Product product
    ) {

        return new CachedProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCreatedAt()
        );
    }

    private ProductResponse toResponse(
            Product product
    ) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCreatedAt()
        );
    }
}