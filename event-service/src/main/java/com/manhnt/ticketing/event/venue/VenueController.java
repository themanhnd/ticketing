package com.manhnt.ticketing.event.venue;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)   // trả 201 thay vì 200 khi tạo mới
    public VenueResponse create(@Valid @RequestBody CreateVenueRequest request) {
        return venueService.create(request);
    }
}