package com.manhnt.ticketing.event.event;

import com.manhnt.ticketing.event.common.BusinessRuleException;
import com.manhnt.ticketing.event.common.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
// import WebMvcTest: dùng Alt+Enter, xem lưu ý bên dưới

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired MockMvcTester mvc;           // "trình duyệt giả" để gửi request vào controller
    @MockitoBean EventService eventService; // service giả, đặt vào Spring context

    @Test
    void createReturns201WithEventBody() {
        when(eventService.create(any())).thenReturn(new EventResponse(
                1L, "Việt Nam vs Thái Lan", EventType.MATCH, Instant.parse("2026-12-20T12:00:00Z"),
                EventStatus.DRAFT, 1L, "Mỹ Đình", List.of()));

        var result = mvc.post().uri("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "venueId": 1, "name": "Việt Nam vs Thái Lan",
                          "type": "MATCH", "startsAt": "2026-12-20T12:00:00Z" }
                        """)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.status").isEqualTo("DRAFT");
    }

    @Test
    void createReturns400WithFieldErrorsWhenRequestInvalid() {
        var result = mvc.post().uri("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "venueId": 1, "name": "", "type": "MATCH", "startsAt": "2020-01-01T00:00:00Z" }
                        """)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.errors.name").isNotNull();
        assertThat(result).bodyJson().extractingPath("$.errors.startsAt").isNotNull(); // ngày trong quá khứ
    }

    @Test
    void getReturns404WhenEventNotFound() {
        when(eventService.get(999L))
                .thenThrow(new ResourceNotFoundException("Không tìm thấy sự kiện 999"));

        assertThat(mvc.get().uri("/api/events/999"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson().extractingPath("$.detail").isEqualTo("Không tìm thấy sự kiện 999");
    }

    @Test
    void publishReturns409WhenBusinessRuleViolated() {
        when(eventService.publish(1L))
                .thenThrow(new BusinessRuleException("Chỉ sự kiện DRAFT mới được mở bán"));

        assertThat(mvc.post().uri("/api/events/1/publish"))
                .hasStatus(HttpStatus.CONFLICT);
    }
}