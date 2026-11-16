package com.manhnt.ticketing.event.event;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@Valid @RequestBody CreateEventRequest request) {
        return eventService.create(request);
    }

    @PostMapping("/{id}/sections")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse addSection(@PathVariable Long id, @Valid @RequestBody AddSectionRequest request) {
        return eventService.addSection(id, request);
    }

    @PostMapping("/{id}/publish")
    public EventResponse publish(@PathVariable Long id) {
        return eventService.publish(id);
    }

    @GetMapping("/{id}")
    public EventResponse get(@PathVariable Long id) {
        return eventService.get(id);
    }

    @GetMapping
    public PagedModel<EventSummaryResponse> list(
            @RequestParam(defaultValue = "ON_SALE") EventStatus status,
            @PageableDefault(size = 20, sort = "startsAt") Pageable pageable) {
        return new PagedModel<>(eventService.list(status, pageable));
    }
}