package com.manhnt.ticketing.inventory.seat;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    boolean existsByEventId(Long eventId);
}