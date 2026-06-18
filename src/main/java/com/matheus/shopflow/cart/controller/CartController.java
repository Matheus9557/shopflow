package com.matheus.shopflow.cart.controller;

import com.matheus.shopflow.cart.dto.AddItemRequest;
import com.matheus.shopflow.cart.dto.CartResponse;
import com.matheus.shopflow.cart.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @PostMapping("/items")
    public CartResponse addItem(@RequestBody AddItemRequest request) {
        return service.addItem(request);
    }
}