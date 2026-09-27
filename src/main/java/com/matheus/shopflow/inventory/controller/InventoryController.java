package com.matheus.shopflow.inventory.controller;

import com.matheus.shopflow.inventory.dto.InventoryRequest;
import com.matheus.shopflow.inventory.dto.InventoryResponse;
import com.matheus.shopflow.inventory.entity.Inventory;
import com.matheus.shopflow.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(
            InventoryService service
    ) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<InventoryResponse> createStock(
            @Valid @RequestBody InventoryRequest request
    ) {

        Inventory inventory =
                service.createStock(
                        request.productId(),
                        request.quantity()
                );

        return ResponseEntity.ok(
                toResponse(inventory)
        );
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getByProductId(
            @PathVariable Long productId
    ) {

        Inventory inventory =
                service.getByProductId(productId);

        return ResponseEntity.ok(
                toResponse(inventory)
        );
    }

    private InventoryResponse toResponse(
            Inventory inventory
    ) {

        return new InventoryResponse(
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );
    }
}