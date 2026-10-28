package com.manhnt.ticketing.inventory.seat;

public record GenerateSeatsResponse(Long eventId, int numberedSeats, int standingCapacity) {
}