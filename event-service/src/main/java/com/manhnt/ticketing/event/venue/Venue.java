package com.manhnt.ticketing.event.venue;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Venue() {
        // JPA bắt buộc có constructor không tham số; để protected cho code khác không dùng nhầm
    }

    public Venue(String name, String address, int capacity) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getCapacity() { return capacity; }
    public Instant getCreatedAt() { return createdAt; }
}