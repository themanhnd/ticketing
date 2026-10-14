package com.manhnt.ticketing.event.event;

import com.manhnt.ticketing.event.common.ResourceNotFoundException;
import com.manhnt.ticketing.event.venue.Venue;
import com.manhnt.ticketing.event.venue.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)   // mặc định mọi method chỉ đọc; method nào ghi thì ghi đè bên dưới
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    public EventService(EventRepository eventRepository, VenueRepository venueRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
    }

    @Transactional
    public EventResponse create(CreateEventRequest request) {
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân " + request.venueId()));
        Event event = eventRepository.save(
                new Event(venue, request.name(), request.type(), request.startsAt()));
        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse addSection(Long eventId, AddSectionRequest request) {
        Event event = findEvent(eventId);
        event.addSection(request.name(), request.seatingType(), request.capacity(), request.price());
        eventRepository.flush();   // ép INSERT ngay để khu khán đài mới có id trong response
        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse publish(Long eventId) {
        Event event = findEvent(eventId);
        event.publish();           // không cần gọi save()!
        return EventResponse.from(event);
    }

    public EventResponse get(Long eventId) {
        return EventResponse.from(findEvent(eventId));
    }

    public Page<EventSummaryResponse> list(EventStatus status, Pageable pageable) {
        return eventRepository.findByStatus(status, pageable).map(EventSummaryResponse::from);
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sự kiện " + eventId));
    }
}