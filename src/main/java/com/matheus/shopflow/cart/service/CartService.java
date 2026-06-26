package com.matheus.shopflow.cart.service;

import com.matheus.shopflow.cart.dto.AddItemRequest;
import com.matheus.shopflow.cart.dto.CartResponse;
import com.matheus.shopflow.cart.entity.Cart;
import com.matheus.shopflow.cart.entity.CartItem;
import com.matheus.shopflow.cart.entity.CartStatus;
import com.matheus.shopflow.cart.repository.CartRepository;
import com.matheus.shopflow.inventory.service.InventoryService;
import com.matheus.shopflow.product.entity.Product;
import com.matheus.shopflow.product.repository.ProductRepository;
import com.matheus.shopflow.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    public CartService(
            CartRepository cartRepository,
            ProductRepository productRepository,
            InventoryService inventoryService
    ) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public CartResponse addItem(AddItemRequest request) {

        Cart cart = cartRepository
                .findByCustomerIdAndStatus(
                        request.customerId(),
                        CartStatus.ACTIVE
                )
                .orElse(new Cart(request.customerId()));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        inventoryService.reserveStock(
                request.productId(),
                request.quantity()
        );

        CartItem newItem = new CartItem(
                product.getId(),
                product.getName(),
                request.quantity(),
                product.getPrice()
        );

        cart.addItem(newItem);

        Cart saved = cartRepository.save(cart);

        int totalItems = saved.getItems()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return new CartResponse(
                saved.getId(),
                saved.getCustomerId(),
                saved.getStatus(),
                totalItems,
                saved.total()
        );
    }
}