package com.matheus.shopflow.reservation.repository;

import com.matheus.shopflow.reservation.entity.Reservation;
import com.matheus.shopflow.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatus(ReservationStatus status);
}