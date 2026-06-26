package com.matheus.shopflow.product.controller;

import com.matheus.shopflow.product.dto.ProductRequest;
import com.matheus.shopflow.product.dto.ProductResponse;
import com.matheus.shopflow.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }
}