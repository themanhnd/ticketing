package com.manhnt.ticketing.event.event;

import java.time.Instant;

// Dùng cho danh sách: gọn hơn, không kéo theo sân và khu khán đài
public record EventSummaryResponse(Long id, String name, EventType type, Instant startsAt, EventStatus status) {
    static EventSummaryResponse from(Event e) {
        return new EventSummaryResponse(e.getId(), e.getName(), e.getType(), e.getStartsAt(), e.getStatus());
    }
}
