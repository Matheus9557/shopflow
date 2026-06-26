package com.matheus.shopflow.inventory.controller;

import com.matheus.shopflow.inventory.dto.InventoryRequest;
import com.matheus.shopflow.inventory.dto.InventoryResponse;
import com.matheus.shopflow.inventory.entity.Inventory;
import com.matheus.shopflow.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createStock(
            @RequestBody InventoryRequest request
    ) {
        Inventory inventory = service.createStock(
                request.productId(),
                request.quantity()
        );

        InventoryResponse response = new InventoryResponse(
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );

        return ResponseEntity.ok(response);
    }
}