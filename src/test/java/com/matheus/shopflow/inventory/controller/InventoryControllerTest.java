package com.matheus.shopflow.inventory.controller;

import com.matheus.shopflow.config.security.SecurityConfig;
import com.matheus.shopflow.inventory.entity.Inventory;
import com.matheus.shopflow.inventory.service.InventoryService;
import com.matheus.shopflow.shared.exception.GlobalExceptionHandler;
import com.matheus.shopflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldCreateInventoryAsAdmin() throws Exception {
        Inventory inventory = new Inventory(1L, 20);

        when(inventoryService.createStock(1L, 20))
                .thenReturn(inventory);

        String request = """
                {
                    "productId": 1,
                    "quantity": 20
                }
                """;

        mockMvc.perform(
                        post("/inventory")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.availableQuantity").value(20))
                .andExpect(jsonPath("$.reservedQuantity").value(0));

        verify(inventoryService).createStock(1L, 20);
    }

    @Test
    void shouldAllowCustomerToReadInventory() throws Exception {
        Inventory inventory = new Inventory(1L, 20);

        when(inventoryService.getByProductId(1L))
                .thenReturn(inventory);

        mockMvc.perform(
                        get("/inventory/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.availableQuantity").value(20))
                .andExpect(jsonPath("$.reservedQuantity").value(0));

        verify(inventoryService).getByProductId(1L);
    }

    @Test
    void shouldAllowAdminToReadInventory() throws Exception {
        Inventory inventory = new Inventory(1L, 20);

        when(inventoryService.getByProductId(1L))
                .thenReturn(inventory);

        mockMvc.perform(
                        get("/inventory/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                )
                .andExpect(status().isOk());

        verify(inventoryService).getByProductId(1L);
    }

    @Test
    void shouldRejectCustomerFromCreatingInventory() throws Exception {
        String request = """
                {
                    "productId": 1,
                    "quantity": 20
                }
                """;

        mockMvc.perform(
                        post("/inventory")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isForbidden());

        verify(inventoryService, never()).createStock(anyLong(), anyInt());
    }

    @Test
    void shouldRejectUnauthenticatedCreate() throws Exception {
        String request = """
                {
                    "productId": 1,
                    "quantity": 20
                }
                """;

        mockMvc.perform(
                        post("/inventory")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isUnauthorized());

        verify(inventoryService, never()).createStock(anyLong(), anyInt());
    }

    @Test
    void shouldRejectUnauthenticatedRead() throws Exception {
        mockMvc.perform(
                        get("/inventory/1")
                )
                .andExpect(status().isUnauthorized());

        verify(inventoryService, never()).getByProductId(anyLong());
    }

    @Test
    void shouldRejectInvalidProductId() throws Exception {
        String request = """
                {
                    "productId": 0,
                    "quantity": 20
                }
                """;

        mockMvc.perform(
                        post("/inventory")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verify(inventoryService, never()).createStock(anyLong(), anyInt());
    }

    @Test
    void shouldRejectNegativeQuantity() throws Exception {
        String request = """
                {
                    "productId": 1,
                    "quantity": -1
                }
                """;

        mockMvc.perform(
                        post("/inventory")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verify(inventoryService, never()).createStock(anyLong(), anyInt());
    }

    @Test
    void shouldAllowZeroQuantity() throws Exception {
        Inventory inventory = new Inventory(1L, 0);

        when(inventoryService.createStock(1L, 0))
                .thenReturn(inventory);

        String request = """
                {
                    "productId": 1,
                    "quantity": 0
                }
                """;

        mockMvc.perform(
                        post("/inventory")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQuantity").value(0));

        verify(inventoryService).createStock(1L, 0);
    }

    @Test
    void shouldReturnNotFoundWhenInventoryDoesNotExist() throws Exception {
        when(inventoryService.getByProductId(999L))
                .thenThrow(new NotFoundException("Inventory not found"));

        mockMvc.perform(
                        get("/inventory/999")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                ))
                )
                .andExpect(status().isNotFound());

        verify(inventoryService).getByProductId(999L);
    }
}