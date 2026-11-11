package com.manhnt.ticketing.inventory.client;

import org.springframework.web.client.ResourceAccessException;
import com.manhnt.ticketing.inventory.common.ResourceNotFoundException;
import com.manhnt.ticketing.inventory.common.ServiceUnavailableException;
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
        try {
            return restClient.get()
                    .uri("/api/events/{id}", eventId)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                        throw new ResourceNotFoundException("Không tìm thấy sự kiện " + eventId);
                    })
                    .body(EventInfo.class);
        } catch (ResourceAccessException ex) {
            throw new ServiceUnavailableException("Không kết nối được tới event-service", ex);
        }
    }
}