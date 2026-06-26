package com.matheus.shopflow.order.service;

import com.matheus.shopflow.cart.entity.Cart;
import com.matheus.shopflow.cart.entity.CartItem;
import com.matheus.shopflow.cart.repository.CartRepository;
import com.matheus.shopflow.order.dto.CheckoutRequest;
import com.matheus.shopflow.order.dto.OrderResponse;
import com.matheus.shopflow.order.entity.Order;
import com.matheus.shopflow.order.entity.OrderItem;
import com.matheus.shopflow.order.repository.OrderRepository;
import com.matheus.shopflow.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public OrderService(
            OrderRepository orderRepository,
            CartRepository cartRepository
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {

        Cart cart = cartRepository.findWithItemsById(request.cartId())
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order(cart.getCustomerId());

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(
                    cartItem.getProductId(),
                    cartItem.getProductName(),
                    cartItem.getQuantity(),
                    cartItem.getUnitPrice()
            );

            order.addItem(orderItem);
        }

        cart.checkout();
        order.markPaymentPending();

        Order saved = orderRepository.save(order);

        int totalItems = saved.getItems()
                .stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        return new OrderResponse(
                saved.getId(),
                saved.getCustomerId(),
                saved.getStatus(),
                totalItems,
                saved.total()
        );
    }
}