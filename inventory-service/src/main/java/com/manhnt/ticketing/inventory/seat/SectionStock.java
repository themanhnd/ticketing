package com.manhnt.ticketing.inventory.seat;

import jakarta.persistence.*;

@Entity
@Table(name = "section_stocks")
public class SectionStock {

    @Id
    private Long sectionId;   // KHÔNG tự sinh: dùng đúng id khu bên event-service

    private Long eventId;
    private int total;
    private int remaining;

    protected SectionStock() {
    }

    SectionStock(Long sectionId, Long eventId, int total) {
        this.sectionId = sectionId;
        this.eventId = eventId;
        this.total = total;
        this.remaining = total;
    }

    public Long getSectionId() { return sectionId; }
    public Long getEventId() { return eventId; }
    public int getTotal() { return total; }
    public int getRemaining() { return remaining; }
}