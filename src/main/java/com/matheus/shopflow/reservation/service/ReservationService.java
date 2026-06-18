package com.matheus.shopflow.reservation.service;

import com.matheus.shopflow.inventory.service.InventoryService;
import com.matheus.shopflow.reservation.dto.ReservationRequest;
import com.matheus.shopflow.reservation.dto.ReservationResponse;
import com.matheus.shopflow.reservation.entity.Reservation;
import com.matheus.shopflow.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ReservationRepository repository;
    private final InventoryService inventoryService;

    public ReservationService(
            ReservationRepository repository,
            InventoryService inventoryService
    ) {
        this.repository = repository;
        this.inventoryService = inventoryService;
    }

    public ReservationResponse createReservation(ReservationRequest request) {

        inventoryService.reserveStock(
                request.productId(),
                request.quantity()
        );

        Reservation reservation = new Reservation(
                request.productId(),
                request.quantity(),
                LocalDateTime.now().plusMinutes(15)
        );

        Reservation saved = repository.save(reservation);

        return new ReservationResponse(
                saved.getId(),
                saved.getProductId(),
                saved.getQuantity(),
                saved.getStatus(),
                saved.getExpiresAt()
        );
    }
}