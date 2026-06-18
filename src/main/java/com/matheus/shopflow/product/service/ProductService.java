package com.matheus.shopflow.product.service;

import com.matheus.shopflow.product.dto.ProductRequest;
import com.matheus.shopflow.product.dto.ProductResponse;
import com.matheus.shopflow.product.entity.Product;
import com.matheus.shopflow.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductResponse create(ProductRequest request) {

        Product product = new Product(
                request.name(),
                request.description(),
                request.price()
        );

        Product saved = repository.save(product);

        return new ProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getPrice(),
                saved.getCreatedAt()
        );
    }
}