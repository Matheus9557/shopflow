package com.matheus.shopflow.product.controller;

import com.matheus.shopflow.product.dto.ProductPageResponse;
import com.matheus.shopflow.product.dto.ProductRequest;
import com.matheus.shopflow.product.dto.ProductResponse;
import com.matheus.shopflow.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(
                service.create(request)
        );
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @GetMapping
    public ResponseEntity<ProductPageResponse> findAll(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @RequestParam(required = false)
            String name,

            @RequestParam(required = false)
            BigDecimal minPrice,

            @RequestParam(required = false)
            BigDecimal maxPrice,

            @RequestParam(defaultValue = "createdAt,desc")
            String sort
    ) {

        return ResponseEntity.ok(
                service.findAll(
                        page,
                        size,
                        name,
                        minPrice,
                        maxPrice,
                        sort
                )
        );
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.getById(id)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(
                service.update(id, request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}