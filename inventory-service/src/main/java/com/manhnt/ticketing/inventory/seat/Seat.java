package com.manhnt.ticketing.inventory.seat;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;
    private Long sectionId;
    private int seatRow;
    private int seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    protected Seat() {
    }

    Seat(Long eventId, Long sectionId, int seatRow, int seatNumber) {
        this.eventId = eventId;
        this.sectionId = sectionId;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.status = SeatStatus.AVAILABLE;
    }

    public Long getId() { return id; }
    public Long getEventId() { return eventId; }
    public Long getSectionId() { return sectionId; }
    public int getSeatRow() { return seatRow; }
    public int getSeatNumber() { return seatNumber; }
    public SeatStatus getStatus() { return status; }
}