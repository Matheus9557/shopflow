package com.matheus.shopflow.product.controller;

import com.matheus.shopflow.config.security.SecurityConfig;
import com.matheus.shopflow.product.dto.ProductPageResponse;
import com.matheus.shopflow.product.dto.ProductResponse;
import com.matheus.shopflow.product.service.ProductService;
import com.matheus.shopflow.shared.exception.BusinessException;
import com.matheus.shopflow.shared.exception.GlobalExceptionHandler;
import com.matheus.shopflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldReturnProductsForCustomer() throws Exception {

        ProductPageResponse response =
                createPageResponse();

        when(productService.findAll(
                eq(0),
                eq(20),
                isNull(),
                isNull(),
                isNull(),
                eq("createdAt,desc")
        )).thenReturn(response);

        mockMvc.perform(
                        get("/products")
                                .with(
                                        jwt().authorities(
                                                () -> "ROLE_CUSTOMER"
                                        )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value("Notebook Gamer Pro"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void shouldReturnProductsForAdmin() throws Exception {

        ProductPageResponse response =
                createPageResponse();

        when(productService.findAll(
                eq(0),
                eq(20),
                isNull(),
                isNull(),
                isNull(),
                eq("createdAt,desc")
        )).thenReturn(response);

        mockMvc.perform(
                        get("/products")
                                .with(
                                        jwt().authorities(
                                                () -> "ROLE_ADMIN"
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements")
                        .value(1));
    }

    @Test
    void shouldRejectRequestWithoutAuthentication() throws Exception {

        mockMvc.perform(
                        get("/products")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldApplyPaginationParameters() throws Exception {

        ProductPageResponse response =
                createPageResponse();

        when(productService.findAll(
                eq(2),
                eq(10),
                isNull(),
                isNull(),
                isNull(),
                eq("createdAt,desc")
        )).thenReturn(response);

        mockMvc.perform(
                        get("/products")
                                .param("page", "2")
                                .param("size", "10")
                                .with(
                                        jwt().authorities(
                                                () -> "ROLE_CUSTOMER"
                                        )
                                )
                )
                .andExpect(status().isOk());

    }

    @Test
    void shouldFilterByName() throws Exception {

        ProductPageResponse response =
                createPageResponse();

        when(productService.findAll(
                eq(0),
                eq(20),
                eq("notebook"),
                isNull(),
                isNull(),
                eq("createdAt,desc")
        )).thenReturn(response);

        mockMvc.perform(
                        get("/products")
                                .param("name", "notebook")
                                .with(
                                        jwt().authorities(
                                                () -> "ROLE_CUSTOMER"
                                        )
                                )
                )
                .andExpect(status().isOk());

    }

    @Test
    void shouldFilterByPriceRange() throws Exception {

        ProductPageResponse response =
                createPageResponse();

        when(productService.findAll(
                eq(0),
                eq(20),
                isNull(),
                eq(new BigDecimal("1000")),
                eq(new BigDecimal("5000")),
                eq("createdAt,desc")
        )).thenReturn(response);

        mockMvc.perform(
                        get("/products")
                                .param("minPrice", "1000")
                                .param("maxPrice", "5000")
                                .with(
                                        jwt().authorities(
                                                () -> "ROLE_CUSTOMER"
                                        )
                                )
                )
                .andExpect(status().isOk());

    }

    @Test
    void shouldApplySorting() throws Exception {

        ProductPageResponse response =
                createPageResponse();

        when(productService.findAll(
                eq(0),
                eq(20),
                isNull(),
                isNull(),
                isNull(),
                eq("price,asc")
        )).thenReturn(response);

        mockMvc.perform(
                        get("/products")
                                .param("sort", "price,asc")
                                .with(
                                        jwt().authorities(
                                                () -> "ROLE_CUSTOMER"
                                        )
                                )
                )
                .andExpect(status().isOk());

    }

    @Test
    void shouldReturnBadRequestForInvalidBusinessParameter()
            throws Exception {

        when(productService.findAll(
                eq(0),
                eq(20),
                isNull(),
                isNull(),
                isNull(),
                eq("invalid,asc")
        )).thenThrow(
                new BusinessException(
                        "Invalid sort field"
                )
        );

        mockMvc.perform(
                        get("/products")
                                .param("sort", "invalid,asc")
                                .with(
                                        jwt().authorities(
                                                () -> "ROLE_CUSTOMER"
                                        )
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid sort field"));
    }

    @Test
    void shouldReturnBadRequestForInvalidPageSize()
            throws Exception {

        when(productService.findAll(
                eq(0),
                eq(51),
                isNull(),
                isNull(),
                isNull(),
                eq("createdAt,desc")
        )).thenThrow(
                new BusinessException(
                        "Page size must be between 1 and 50"
                )
        );

        mockMvc.perform(
                        get("/products")
                                .param("size", "51")
                                .with(
                                        jwt().authorities(
                                                () -> "ROLE_CUSTOMER"
                                        )
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Page size must be between 1 and 50"
                        ));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist()
            throws Exception {

        when(productService.getById(999L))
                .thenThrow(
                        new NotFoundException(
                                "Product not found"
                        )
                );

        mockMvc.perform(
                        get("/products/999")
                                .with(
                                        jwt().authorities(
                                                () -> "ROLE_CUSTOMER"
                                        )
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Product not found"));
    }

    private ProductPageResponse createPageResponse() {

        ProductResponse product =
                new ProductResponse(
                        1L,
                        "Notebook Gamer Pro",
                        "Notebook para jogos",
                        new BigDecimal("4500.00"),
                        LocalDateTime.of(
                                2026,
                                9,
                                27,
                                14,
                                0
                        )
                );

        return new ProductPageResponse(
                List.of(product),
                0,
                20,
                1,
                1,
                true,
                true
        );
    }
}