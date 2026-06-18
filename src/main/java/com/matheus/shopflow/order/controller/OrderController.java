package com.matheus.shopflow.order.controller;

import com.matheus.shopflow.order.dto.CheckoutRequest;
import com.matheus.shopflow.order.dto.OrderResponse;
import com.matheus.shopflow.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/checkout")
    public OrderResponse checkout(@RequestBody CheckoutRequest request) {
        return service.checkout(request);
    }
}