package com.manhnt.ticketing.event.venue;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    // Constructor injection: Spring tự truyền VenueRepository vào, không cần @Autowired
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Transactional
    public VenueResponse create(CreateVenueRequest request) {
        Venue venue = venueRepository.save(
                new Venue(request.name(), request.address(), request.capacity()));
        return VenueResponse.from(venue);
    }
}