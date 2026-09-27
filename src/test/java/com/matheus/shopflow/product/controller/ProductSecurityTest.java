package com.matheus.shopflow.product.controller;

import com.matheus.shopflow.config.security.SecurityConfig;
import com.matheus.shopflow.product.controller.ProductController;
import com.matheus.shopflow.product.dto.ProductResponse;
import com.matheus.shopflow.product.service.ProductService;
import com.matheus.shopflow.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(ProductController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
class ProductSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtDecoder jwtDecoder;

    // ============================================================
    // GET /products/{id}
    // ============================================================

    @Test
    void shouldAllowCustomerGettingProduct() throws Exception {

        when(productService.getById(1L))
                .thenReturn(null);

        mockMvc.perform(
                        get("/products/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                ))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAdminGettingProduct() throws Exception {

        when(productService.getById(1L))
                .thenReturn(null);

        mockMvc.perform(
                        get("/products/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectUnauthenticatedGettingProduct() throws Exception {

        mockMvc.perform(
                        get("/products/1")
                )
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // POST /products
    // ============================================================

    @Test
    void shouldRejectCustomerCreatingProduct() throws Exception {

        mockMvc.perform(
                        post("/products")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Notebook",
                                          "price": 3000,
                                          "stockQuantity": 5
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminCreatingProduct() throws Exception {

        when(productService.create(any()))
                .thenReturn(null);

        mockMvc.perform(
                        post("/products")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Notebook",
                                          "price": 3000,
                                          "stockQuantity": 5
                                        }
                                        """)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectUnauthenticatedCreatingProduct() throws Exception {

        mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Notebook",
                                          "price": 3000,
                                          "stockQuantity": 5
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // PUT /products/{id}
    // ============================================================

    @Test
    void shouldRejectCustomerUpdatingProduct() throws Exception {

        mockMvc.perform(
                        put("/products/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Notebook Updated",
                                          "price": 3500,
                                          "stockQuantity": 10
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminUpdatingProduct() throws Exception {

        when(productService.update(anyLong(), any()))
                .thenReturn(null);

        mockMvc.perform(
                        put("/products/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Notebook Updated",
                                          "price": 3500,
                                          "stockQuantity": 10
                                        }
                                        """)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectUnauthenticatedUpdatingProduct() throws Exception {

        mockMvc.perform(
                        put("/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "Notebook Updated",
                                          "price": 3500,
                                          "stockQuantity": 10
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // DELETE /products/{id}
    // ============================================================

    @Test
    void shouldRejectCustomerDeletingProduct() throws Exception {

        mockMvc.perform(
                        delete("/products/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CUSTOMER")
                                ))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminDeletingProduct() throws Exception {

        mockMvc.perform(
                        delete("/products/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")
                                ))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectUnauthenticatedDeletingProduct() throws Exception {

        mockMvc.perform(
                        delete("/products/1")
                )
                .andExpect(status().isUnauthorized());
    }
}
