package com.manhnt.ticketing.event.event;

import com.manhnt.ticketing.event.TestcontainersConfiguration;
import com.manhnt.ticketing.event.venue.Venue;
import com.manhnt.ticketing.event.venue.VenueRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)   // PostgreSQL thật, chạy trong Docker
@Transactional                               // mỗi test xong tự rollback, DB sạch cho test sau
class EventRepositoryTest {

    @Autowired VenueRepository venueRepository;
    @Autowired EventRepository eventRepository;
    @Autowired EntityManager entityManager;

    @Test
    void savesEventTogetherWithSections() {
        Venue venue = venueRepository.save(
                new Venue("Sân vận động Mỹ Đình", "Nam Từ Liêm, Hà Nội", 40_000));

        Event event = new Event(venue, "Việt Nam vs Thái Lan",
                EventType.MATCH, Instant.parse("2026-12-20T12:00:00Z"));
        event.addSection("Khán đài A", SeatingType.NUMBERED, 5_000, new BigDecimal("500000"));
        event.addSection("Khán đài B", SeatingType.GENERAL, 8_000, new BigDecimal("300000"));
        eventRepository.save(event);

        entityManager.flush();   // ép ghi xuống DB ngay
        entityManager.clear();   // xoá bộ nhớ đệm để lần đọc sau là đọc thật từ DB

        Event found = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(found.getSections()).hasSize(2);
        assertThat(found.getStatus()).isEqualTo(EventStatus.DRAFT);
    }
}