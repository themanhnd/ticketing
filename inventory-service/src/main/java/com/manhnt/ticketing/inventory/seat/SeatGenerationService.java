package com.manhnt.ticketing.inventory.seat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manhnt.ticketing.inventory.client.EventClient;
import com.manhnt.ticketing.inventory.client.EventInfo;
import com.manhnt.ticketing.inventory.client.SectionInfo;
import com.manhnt.ticketing.inventory.common.BusinessRuleException;

@Service
public class SeatGenerationService {

    // Đơn giản hoá: mỗi hàng 50 ghế. Thực tế sơ đồ sân phức tạp hơn nhiều.
    static final int SEATS_PER_ROW = 50;

    private final EventClient eventClient;
    private final SeatRepository seatRepository;
    private final SectionStockRepository stockRepository;

    public SeatGenerationService(EventClient eventClient, SeatRepository seatRepository,
                                 SectionStockRepository stockRepository) {
        this.eventClient = eventClient;
        this.seatRepository = seatRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional
    public GenerateSeatsResponse generate(Long eventId) {
        if (seatRepository.existsByEventId(eventId) || stockRepository.existsByEventId(eventId)) {
            throw new BusinessRuleException("Sự kiện " + eventId + " đã được sinh ghế");
        }

        EventInfo event = eventClient.getEvent(eventId);   // ← lời gọi sang event-service

        if (!"ON_SALE".equals(event.status())) {
            throw new BusinessRuleException("Chỉ sinh ghế cho sự kiện đã mở bán (ON_SALE)");
        }

        int numberedSeats = 0;
        int standingCapacity = 0;
        for (SectionInfo section : event.sections()) {
            switch (section.seatingType()) {
                case NUMBERED -> {
                    List<Seat> seats = buildSeats(eventId, section);
                    seatRepository.saveAll(seats);
                    numberedSeats += seats.size();
                }
                case GENERAL -> {
                    stockRepository.save(new SectionStock(section.id(), eventId, section.capacity()));
                    standingCapacity += section.capacity();
                }
            }
        }
        return new GenerateSeatsResponse(eventId, numberedSeats, standingCapacity);
    }

    private List<Seat> buildSeats(Long eventId, SectionInfo section) {
        List<Seat> seats = new ArrayList<>(section.capacity());
        for (int i = 0; i < section.capacity(); i++) {
            int row = i / SEATS_PER_ROW + 1;
            int number = i % SEATS_PER_ROW + 1;
            seats.add(new Seat(eventId, section.id(), row, number));
        }
        return seats;
    }
}