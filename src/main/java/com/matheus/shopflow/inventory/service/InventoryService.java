package com.matheus.shopflow.inventory.service;

import com.matheus.shopflow.inventory.entity.Inventory;
import com.matheus.shopflow.inventory.repository.InventoryRepository;
import com.matheus.shopflow.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public Inventory createStock(Long productId, int quantity) {
        Inventory inventory = new Inventory(productId, quantity);
        return repository.save(inventory);
    }

    public void reserveStock(Long productId, int quantity) {
        Inventory inventory = repository.findLockedByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found"));

        inventory.reserve(quantity);
    }

    public void releaseStock(Long productId, int quantity) {
        Inventory inventory = repository.findLockedByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found"));

        inventory.release(quantity);
    }

    public void confirmReservation(Long productId, int quantity) {
        Inventory inventory = repository.findLockedByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found"));

        inventory.confirmReservation(quantity);
    }
}