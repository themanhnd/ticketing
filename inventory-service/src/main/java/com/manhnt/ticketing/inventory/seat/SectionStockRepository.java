package com.manhnt.ticketing.inventory.seat;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionStockRepository extends JpaRepository<SectionStock, Long> {
    boolean existsByEventId(Long eventId);
}