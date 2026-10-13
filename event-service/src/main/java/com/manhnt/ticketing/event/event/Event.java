package com.manhnt.ticketing.event.event;

import com.manhnt.ticketing.event.venue.Venue;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Venue venue;                       // tự map sang cột venue_id

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventType type;

    @Column(nullable = false)
    private Instant startsAt;                  // tự map sang cột starts_at

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status = EventStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Event() {}

    public Event(Venue venue, String name, EventType type, Instant startsAt) {
        this.venue = venue;
        this.name = name;
        this.type = type;
        this.startsAt = startsAt;
    }

    public Section addSection(String name, SeatingType seatingType, int capacity, BigDecimal price) {
        if (status != EventStatus.DRAFT) {
            throw new IllegalStateException("Chỉ được thêm khu khán đài khi sự kiện đang ở trạng thái DRAFT");
        }
        int totalCapacity = sections.stream().mapToInt(Section::getCapacity).sum() + capacity;
        if (totalCapacity > venue.getCapacity()) {
            throw new IllegalStateException("Tổng sức chứa các khu vượt quá sức chứa của sân");
        }
        Section section = new Section(this, name, seatingType, capacity, price);
        sections.add(section);
        return section;
    }

    public void publish() {
        if (status != EventStatus.DRAFT) {
            throw new IllegalStateException("Chỉ sự kiện DRAFT mới được mở bán");
        }
        if (sections.isEmpty()) {
            throw new IllegalStateException("Phải có ít nhất 1 khu khán đài trước khi mở bán");
        }
        status = EventStatus.ON_SALE;
    }

    public Long getId() { return id; }
    public Venue getVenue() { return venue; }
    public String getName() { return name; }
    public EventType getType() { return type; }
    public Instant getStartsAt() { return startsAt; }
    public EventStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public List<Section> getSections() { return Collections.unmodifiableList(sections); }
}