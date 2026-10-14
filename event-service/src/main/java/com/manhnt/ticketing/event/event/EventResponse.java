package com.manhnt.ticketing.event.event;

import java.time.Instant;
import java.util.List;

// Chi tiết một sự kiện: kèm sân và các khu khán đài
public record EventResponse(Long id, String name, EventType type, Instant startsAt, EventStatus status,
                            Long venueId, String venueName, List<SectionResponse> sections) {
    static EventResponse from(Event e) {
        return new EventResponse(e.getId(), e.getName(), e.getType(), e.getStartsAt(), e.getStatus(),
                e.getVenue().getId(), e.getVenue().getName(),
                e.getSections().stream().map(SectionResponse::from).toList());
    }
}
