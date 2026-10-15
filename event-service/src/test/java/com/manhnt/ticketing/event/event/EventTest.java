package com.manhnt.ticketing.event.event;

import com.manhnt.ticketing.event.common.BusinessRuleException;
import com.manhnt.ticketing.event.venue.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventTest {

    private Event event;

    @BeforeEach   // chạy trước MỖI test, nên mỗi test có một Event mới tinh
    void setUp() {
        Venue venue = new Venue("Mỹ Đình", "Hà Nội", 40_000);
        event = new Event(venue, "Việt Nam vs Thái Lan", EventType.MATCH,
                Instant.now().plus(30, ChronoUnit.DAYS));
    }

    @Test
    void newEventIsDraft() {
        assertThat(event.getStatus()).isEqualTo(EventStatus.DRAFT);
    }

    @Test
    void publishSucceedsWhenEventHasSections() {
        event.addSection("Khán đài A", SeatingType.NUMBERED, 5_000, new BigDecimal("500000"));

        event.publish();

        assertThat(event.getStatus()).isEqualTo(EventStatus.ON_SALE);
    }

    @Test
    void cannotPublishWithoutSections() {
        assertThatThrownBy(event::publish)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ít nhất 1 khu");
    }

    @Test
    void cannotPublishTwice() {
        event.addSection("Khán đài A", SeatingType.NUMBERED, 5_000, new BigDecimal("500000"));
        event.publish();

        assertThatThrownBy(event::publish).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cannotAddSectionAfterPublishing() {
        event.addSection("Khán đài A", SeatingType.NUMBERED, 5_000, new BigDecimal("500000"));
        event.publish();

        assertThatThrownBy(() ->
                event.addSection("Khán đài B", SeatingType.GENERAL, 1_000, new BigDecimal("100000")))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cannotExceedVenueCapacity() {
        event.addSection("Khán đài A", SeatingType.GENERAL, 30_000, new BigDecimal("100000"));

        assertThatThrownBy(() ->
                event.addSection("Khán đài B", SeatingType.GENERAL, 10_001, new BigDecimal("100000")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("sức chứa");
    }


}