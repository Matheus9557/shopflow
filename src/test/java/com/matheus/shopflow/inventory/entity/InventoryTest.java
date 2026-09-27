package com.matheus.shopflow.inventory.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    void shouldCreateInventoryWithInitialQuantity() {

        Inventory inventory =
                new Inventory(1L, 10);

        assertEquals(
                1L,
                inventory.getProductId()
        );

        assertEquals(
                10,
                inventory.getAvailableQuantity()
        );

        assertEquals(
                0,
                inventory.getReservedQuantity()
        );
    }

    @Test
    void shouldAllowZeroInitialStock() {

        Inventory inventory =
                new Inventory(1L, 0);

        assertEquals(
                0,
                inventory.getAvailableQuantity()
        );
    }

    @Test
    void shouldAddStock() {

        Inventory inventory =
                new Inventory(1L, 10);

        inventory.addStock(5);

        assertEquals(
                15,
                inventory.getAvailableQuantity()
        );
    }

    @Test
    void shouldRemoveAvailableStock() {

        Inventory inventory =
                new Inventory(1L, 10);

        inventory.removeStock(4);

        assertEquals(
                6,
                inventory.getAvailableQuantity()
        );
    }

    @Test
    void shouldReserveStock() {

        Inventory inventory =
                new Inventory(1L, 10);

        inventory.reserve(4);

        assertEquals(
                6,
                inventory.getAvailableQuantity()
        );

        assertEquals(
                4,
                inventory.getReservedQuantity()
        );
    }

    @Test
    void shouldReleaseReservedStock() {

        Inventory inventory =
                new Inventory(1L, 10);

        inventory.reserve(4);
        inventory.release(2);

        assertEquals(
                8,
                inventory.getAvailableQuantity()
        );

        assertEquals(
                2,
                inventory.getReservedQuantity()
        );
    }

    @Test
    void shouldConfirmReservation() {

        Inventory inventory =
                new Inventory(1L, 10);

        inventory.reserve(4);
        inventory.confirmReservation(4);

        assertEquals(
                6,
                inventory.getAvailableQuantity()
        );

        assertEquals(
                0,
                inventory.getReservedQuantity()
        );
    }

    @Test
    void shouldRejectNegativeInitialStock() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Inventory(1L, -1)
        );
    }

    @Test
    void shouldRejectInvalidProductId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Inventory(0L, 10)
        );
    }

    @Test
    void shouldRejectReservationWithoutEnoughStock() {

        Inventory inventory =
                new Inventory(1L, 5);

        assertThrows(
                IllegalStateException.class,
                () -> inventory.reserve(6)
        );
    }

    @Test
    void shouldRejectReleaseWithoutEnoughReservedStock() {

        Inventory inventory =
                new Inventory(1L, 5);

        assertThrows(
                IllegalStateException.class,
                () -> inventory.release(1)
        );
    }

    @Test
    void shouldRejectConfirmWithoutEnoughReservedStock() {

        Inventory inventory =
                new Inventory(1L, 5);

        assertThrows(
                IllegalStateException.class,
                () -> inventory.confirmReservation(1)
        );
    }

    @Test
    void shouldRejectRemovingMoreThanAvailableStock() {

        Inventory inventory =
                new Inventory(1L, 5);

        assertThrows(
                IllegalStateException.class,
                () -> inventory.removeStock(6)
        );
    }

    @Test
    void shouldRejectZeroQuantity() {

        Inventory inventory =
                new Inventory(1L, 5);

        assertThrows(
                IllegalArgumentException.class,
                () -> inventory.reserve(0)
        );
    }
}