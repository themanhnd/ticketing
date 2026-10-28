package com.manhnt.ticketing.inventory.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EventClient {

    private final RestClient restClient;

    public EventClient(RestClient.Builder builder,
                       @Value("${ticketing.services.event.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public EventInfo getEvent(Long eventId) {
        return restClient.get()
                .uri("/api/events/{id}", eventId)
                .retrieve()
                .body(EventInfo.class);
    }
}