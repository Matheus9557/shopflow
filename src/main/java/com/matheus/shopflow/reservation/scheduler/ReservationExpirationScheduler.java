package com.matheus.shopflow.reservation.scheduler;

import com.matheus.shopflow.reservation.service.ReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpirationScheduler {

    private final ReservationService reservationService;

    public ReservationExpirationScheduler(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    @Scheduled(fixedRate = 30000)
    public void processExpiredReservations() {
        reservationService.expireReservations();
    }
}