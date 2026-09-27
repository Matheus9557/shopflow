package com.matheus.shopflow.inventory.service;

import com.matheus.shopflow.inventory.entity.Inventory;
import com.matheus.shopflow.inventory.repository.InventoryRepository;
import com.matheus.shopflow.product.repository.ProductRepository;
import com.matheus.shopflow.shared.exception.BusinessException;
import com.matheus.shopflow.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository
    ) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public Inventory createStock(
            Long productId,
            int quantity
    ) {

        validateProductExists(productId);

        if (inventoryRepository.existsByProductId(productId)) {
            throw new BusinessException(
                    "Inventory already exists for product"
            );
        }

        Inventory inventory =
                new Inventory(
                        productId,
                        quantity
                );

        return inventoryRepository.save(inventory);
    }

    @Transactional(readOnly = true)
    public Inventory getByProductId(Long productId) {

        return inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Inventory not found"
                        )
                );
    }

    public void addStock(
            Long productId,
            int quantity
    ) {

        Inventory inventory =
                findLockedInventory(productId);

        inventory.addStock(quantity);
    }

    public void removeStock(
            Long productId,
            int quantity
    ) {

        Inventory inventory =
                findLockedInventory(productId);

        inventory.removeStock(quantity);
    }

    public void reserveStock(
            Long productId,
            int quantity
    ) {

        Inventory inventory =
                findLockedInventory(productId);

        inventory.reserve(quantity);
    }

    public void releaseStock(
            Long productId,
            int quantity
    ) {

        Inventory inventory =
                findLockedInventory(productId);

        inventory.release(quantity);
    }

    public void confirmReservation(
            Long productId,
            int quantity
    ) {

        Inventory inventory =
                findLockedInventory(productId);

        inventory.confirmReservation(quantity);
    }

    private Inventory findLockedInventory(
            Long productId
    ) {

        return inventoryRepository
                .findLockedByProductId(productId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Inventory not found"
                        )
                );
    }

    private void validateProductExists(
            Long productId
    ) {

        if (!productRepository.existsById(productId)) {
            throw new NotFoundException(
                    "Product not found"
            );
        }
    }
}