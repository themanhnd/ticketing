package com.manhnt.ticketing.event.event;

import java.math.BigDecimal;

public record SectionResponse(Long id, String name, SeatingType seatingType, int capacity, BigDecimal price) {
    static SectionResponse from(Section s) {
        return new SectionResponse(s.getId(), s.getName(), s.getSeatingType(), s.getCapacity(), s.getPrice());
    }
}
