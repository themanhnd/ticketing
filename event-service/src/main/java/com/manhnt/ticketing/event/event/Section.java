package com.manhnt.ticketing.event.event;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Event event;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatingType seatingType;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    protected Section() {}

    // không public: chỉ Event.addSection() được tạo Section
    Section(Event event, String name, SeatingType seatingType, int capacity, BigDecimal price) {
        this.event = event;
        this.name = name;
        this.seatingType = seatingType;
        this.capacity = capacity;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public SeatingType getSeatingType() { return seatingType; }
    public int getCapacity() { return capacity; }
    public BigDecimal getPrice() { return price; }
}