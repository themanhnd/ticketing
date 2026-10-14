package com.manhnt.ticketing.event.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateEventRequest(
        @NotNull Long venueId,
        @NotBlank @Size(max = 200) String name,
        @NotNull EventType type,
        @NotNull @Future Instant startsAt      // @Future: không tạo được sự kiện trong quá khứ
) {}