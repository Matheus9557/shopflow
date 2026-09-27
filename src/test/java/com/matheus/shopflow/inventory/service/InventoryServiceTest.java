package com.matheus.shopflow.inventory.service;

import com.matheus.shopflow.inventory.entity.Inventory;
import com.matheus.shopflow.inventory.repository.InventoryRepository;
import com.matheus.shopflow.product.repository.ProductRepository;
import com.matheus.shopflow.shared.exception.BusinessException;
import com.matheus.shopflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    private InventoryRepository inventoryRepository;
    private ProductRepository productRepository;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {

        inventoryRepository =
                mock(InventoryRepository.class);

        productRepository =
                mock(ProductRepository.class);

        inventoryService =
                new InventoryService(
                        inventoryRepository,
                        productRepository
                );
    }

    @Test
    void shouldCreateStockForExistingProduct() {

        Long productId = 1L;
        int quantity = 10;

        when(productRepository.existsById(productId))
                .thenReturn(true);

        when(inventoryRepository.existsByProductId(productId))
                .thenReturn(false);

        Inventory savedInventory =
                new Inventory(productId, quantity);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(savedInventory);

        Inventory result =
                inventoryService.createStock(
                        productId,
                        quantity
                );

        assertNotNull(result);

        assertEquals(
                productId,
                result.getProductId()
        );

        assertEquals(
                quantity,
                result.getAvailableQuantity()
        );

        assertEquals(
                0,
                result.getReservedQuantity()
        );

        verify(productRepository)
                .existsById(productId);

        verify(inventoryRepository)
                .existsByProductId(productId);

        verify(inventoryRepository)
                .save(any(Inventory.class));
    }

    @Test
    void shouldRejectStockForNonExistingProduct() {

        Long productId = 999L;

        when(productRepository.existsById(productId))
                .thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> inventoryService.createStock(
                        productId,
                        10
                )
        );

        verify(productRepository)
                .existsById(productId);

        verify(inventoryRepository, never())
                .existsByProductId(anyLong());

        verify(inventoryRepository, never())
                .save(any(Inventory.class));
    }

    @Test
    void shouldRejectDuplicateInventory() {

        Long productId = 1L;

        when(productRepository.existsById(productId))
                .thenReturn(true);

        when(inventoryRepository.existsByProductId(productId))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> inventoryService.createStock(
                        productId,
                        10
                )
        );

        verify(productRepository)
                .existsById(productId);

        verify(inventoryRepository)
                .existsByProductId(productId);

        verify(inventoryRepository, never())
                .save(any(Inventory.class));
    }

    @Test
    void shouldGetInventoryByProductId() {

        Long productId = 1L;

        Inventory inventory =
                new Inventory(productId, 10);

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        Inventory result =
                inventoryService.getByProductId(productId);

        assertNotNull(result);

        assertEquals(
                productId,
                result.getProductId()
        );

        assertEquals(
                10,
                result.getAvailableQuantity()
        );

        verify(inventoryRepository)
                .findByProductId(productId);
    }

    @Test
    void shouldThrowWhenInventoryDoesNotExist() {

        Long productId = 999L;

        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> inventoryService.getByProductId(productId)
        );

        verify(inventoryRepository)
                .findByProductId(productId);
    }

    @Test
    void shouldAddStockUsingLockedInventory() {

        Long productId = 1L;

        Inventory inventory =
                new Inventory(productId, 10);

        when(inventoryRepository
                .findLockedByProductId(productId))
                .thenReturn(Optional.of(inventory));

        inventoryService.addStock(
                productId,
                5
        );

        assertEquals(
                15,
                inventory.getAvailableQuantity()
        );

        verify(inventoryRepository)
                .findLockedByProductId(productId);
    }

    @Test
    void shouldRemoveStockUsingLockedInventory() {

        Long productId = 1L;

        Inventory inventory =
                new Inventory(productId, 10);

        when(inventoryRepository
                .findLockedByProductId(productId))
                .thenReturn(Optional.of(inventory));

        inventoryService.removeStock(
                productId,
                4
        );

        assertEquals(
                6,
                inventory.getAvailableQuantity()
        );

        verify(inventoryRepository)
                .findLockedByProductId(productId);
    }

    @Test
    void shouldReserveStockUsingLockedInventory() {

        Long productId = 1L;

        Inventory inventory =
                new Inventory(productId, 10);

        when(inventoryRepository
                .findLockedByProductId(productId))
                .thenReturn(Optional.of(inventory));

        inventoryService.reserveStock(
                productId,
                4
        );

        assertEquals(
                6,
                inventory.getAvailableQuantity()
        );

        assertEquals(
                4,
                inventory.getReservedQuantity()
        );

        verify(inventoryRepository)
                .findLockedByProductId(productId);
    }

    @Test
    void shouldReleaseStockUsingLockedInventory() {

        Long productId = 1L;

        Inventory inventory =
                new Inventory(productId, 10);

        inventory.reserve(4);

        when(inventoryRepository
                .findLockedByProductId(productId))
                .thenReturn(Optional.of(inventory));

        inventoryService.releaseStock(
                productId,
                2
        );

        assertEquals(
                8,
                inventory.getAvailableQuantity()
        );

        assertEquals(
                2,
                inventory.getReservedQuantity()
        );

        verify(inventoryRepository)
                .findLockedByProductId(productId);
    }

    @Test
    void shouldConfirmReservationUsingLockedInventory() {

        Long productId = 1L;

        Inventory inventory =
                new Inventory(productId, 10);

        inventory.reserve(4);

        when(inventoryRepository
                .findLockedByProductId(productId))
                .thenReturn(Optional.of(inventory));

        inventoryService.confirmReservation(
                productId,
                4
        );

        assertEquals(
                6,
                inventory.getAvailableQuantity()
        );

        assertEquals(
                0,
                inventory.getReservedQuantity()
        );

        verify(inventoryRepository)
                .findLockedByProductId(productId);
    }

    @Test
    void shouldThrowWhenLockedInventoryDoesNotExist() {

        Long productId = 999L;

        when(inventoryRepository
                .findLockedByProductId(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> inventoryService.reserveStock(
                        productId,
                        1
                )
        );

        verify(inventoryRepository)
                .findLockedByProductId(productId);
    }

    @Test
    void shouldNotSaveInventoryWhenCreatingDuplicateStock() {

        Long productId = 1L;

        when(productRepository.existsById(productId))
                .thenReturn(true);

        when(inventoryRepository.existsByProductId(productId))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> inventoryService.createStock(
                        productId,
                        20
                )
        );

        verify(inventoryRepository, never())
                .save(any(Inventory.class));
    }

    @Test
    void shouldCreateInventoryWithCorrectValues() {

        Long productId = 5L;
        int quantity = 25;

        when(productRepository.existsById(productId))
                .thenReturn(true);

        when(inventoryRepository.existsByProductId(productId))
                .thenReturn(false);

        ArgumentCaptor<Inventory> captor =
                ArgumentCaptor.forClass(Inventory.class);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0)
                );

        inventoryService.createStock(
                productId,
                quantity
        );

        verify(inventoryRepository)
                .save(captor.capture());

        Inventory captured =
                captor.getValue();

        assertEquals(
                productId,
                captured.getProductId()
        );

        assertEquals(
                quantity,
                captured.getAvailableQuantity()
        );

        assertEquals(
                0,
                captured.getReservedQuantity()
        );
    }
}