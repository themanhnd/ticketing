package com.manhnt.ticketing.event.event;

import com.manhnt.ticketing.event.common.ResourceNotFoundException;
import com.manhnt.ticketing.event.venue.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock EventRepository eventRepository;      // repository giả
    @Mock VenueRepository venueRepository;      // repository giả
    @InjectMocks EventService eventService;     // service thật, được truyền 2 mock ở trên

    @Test
    void createFailsWhenVenueDoesNotExist() {
        when(venueRepository.findById(99L)).thenReturn(Optional.empty());   // "giả vờ" không có sân 99
        var request = new CreateEventRequest(99L, "Trận A", EventType.MATCH,
                Instant.now().plus(30, ChronoUnit.DAYS));

        assertThatThrownBy(() -> eventService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(eventRepository, never()).save(any());   // không được lưu sự kiện khi thiếu sân
    }

    @Test
    void publishFailsWhenEventDoesNotExist() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.publish(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}