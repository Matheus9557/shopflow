package com.matheus.shopflow.reservation.controller;

import com.matheus.shopflow.reservation.dto.ReservationRequest;
import com.matheus.shopflow.reservation.dto.ReservationResponse;
import com.matheus.shopflow.reservation.service.ReservationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    public ReservationResponse create(
            @RequestBody ReservationRequest request
    ) {
        return service.createReservation(request);
    }
}