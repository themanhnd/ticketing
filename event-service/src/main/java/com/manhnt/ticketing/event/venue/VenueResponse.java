package com.manhnt.ticketing.event.venue;

public record VenueResponse(Long id, String name, String address, int capacity) {

    static VenueResponse from(Venue venue) {
        return new VenueResponse(venue.getId(), venue.getName(), venue.getAddress(), venue.getCapacity());
    }
}