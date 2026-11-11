package com.manhnt.ticketing.inventory.seat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.manhnt.ticketing.inventory.client.EventInfo;
import com.manhnt.ticketing.inventory.client.SectionInfo;

@Component
public class SeatWriter {

    static final int SEATS_PER_ROW = 50;

    private final SeatRepository seatRepository;
    private final SectionStockRepository stockRepository;

    public SeatWriter(SeatRepository seatRepository, SectionStockRepository stockRepository) {
        this.seatRepository = seatRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional
    public GenerateSeatsResponse write(EventInfo event) {
        int numberedSeats = 0;
        int standingCapacity = 0;
        for (SectionInfo section : event.sections()) {
            switch (section.seatingType()) {
                case NUMBERED -> {
                    List<Seat> seats = buildSeats(event.id(), section);
                    seatRepository.saveAll(seats);
                    numberedSeats += seats.size();
                }
                case GENERAL -> {
                    stockRepository.save(new SectionStock(section.id(), event.id(), section.capacity()));
                    standingCapacity += section.capacity();
                }
            }
        }
        return new GenerateSeatsResponse(event.id(), numberedSeats, standingCapacity);
    }

    private List<Seat> buildSeats(Long eventId, SectionInfo section) {
        List<Seat> seats = new ArrayList<>(section.capacity());
        for (int i = 0; i < section.capacity(); i++) {
            seats.add(new Seat(eventId, section.id(), i / SEATS_PER_ROW + 1, i % SEATS_PER_ROW + 1));
        }
        return seats;
    }
}