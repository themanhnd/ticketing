package com.manhnt.ticketing.inventory.seat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/seats")
public class SeatController {

    private final SeatGenerationService generationService;

    public SeatController(SeatGenerationService generationService) {
        this.generationService = generationService;
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public GenerateSeatsResponse generate(@PathVariable Long eventId) {
        return generationService.generate(eventId);
    }
}