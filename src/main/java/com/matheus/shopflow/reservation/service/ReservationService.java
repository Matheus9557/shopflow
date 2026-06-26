package com.matheus.shopflow.reservation.service;

import com.matheus.shopflow.inventory.service.InventoryService;
import com.matheus.shopflow.reservation.dto.ReservationRequest;
import com.matheus.shopflow.reservation.dto.ReservationResponse;
import com.matheus.shopflow.reservation.entity.Reservation;
import com.matheus.shopflow.reservation.entity.ReservationStatus;
import com.matheus.shopflow.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
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

    public void expireReservations() {

        List<Reservation> expiredReservations =
                repository.findByStatusAndExpiresAtBefore(
                        ReservationStatus.ACTIVE,
                        LocalDateTime.now()
                );

        for (Reservation reservation : expiredReservations) {
            reservation.expire();

            inventoryService.releaseStock(
                    reservation.getProductId(),
                    reservation.getQuantity()
            );

            repository.save(reservation);
        }
    }

    public void confirmReservation(Long reservationId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.confirm();

        inventoryService.confirmReservation(
                reservation.getProductId(),
                reservation.getQuantity()
        );

        repository.save(reservation);
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.cancel();

        inventoryService.releaseStock(
                reservation.getProductId(),
                reservation.getQuantity()
        );

        repository.save(reservation);
    }
}