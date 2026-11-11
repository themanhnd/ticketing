package com.manhnt.ticketing.inventory.seat;

import org.springframework.stereotype.Service;

import com.manhnt.ticketing.inventory.client.EventClient;
import com.manhnt.ticketing.inventory.client.EventInfo;
import com.manhnt.ticketing.inventory.common.BusinessRuleException;

@Service
public class SeatGenerationService {

    private final EventClient eventClient;
    private final SeatRepository seatRepository;
    private final SectionStockRepository stockRepository;
    private final SeatWriter seatWriter;

    public SeatGenerationService(EventClient eventClient, SeatRepository seatRepository,
                                 SectionStockRepository stockRepository, SeatWriter seatWriter) {
        this.eventClient = eventClient;
        this.seatRepository = seatRepository;
        this.stockRepository = stockRepository;
        this.seatWriter = seatWriter;
    }

    // KHÔNG có @Transactional
    public GenerateSeatsResponse generate(Long eventId) {
        if (seatRepository.existsByEventId(eventId) || stockRepository.existsByEventId(eventId)) {
            throw new BusinessRuleException("Sự kiện " + eventId + " đã được sinh ghế");
        }

        EventInfo event = eventClient.getEvent(eventId);   // 1. gọi mạng: không giữ kết nối DB

        if (!"ON_SALE".equals(event.status())) {
            throw new BusinessRuleException("Chỉ sinh ghế cho sự kiện đã mở bán (ON_SALE)");
        }

        return seatWriter.write(event);                     // 2. chỉ phần ghi mới nằm trong transaction
    }
}