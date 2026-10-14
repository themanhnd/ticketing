package com.manhnt.ticketing.event.event;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AddSectionRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull SeatingType seatingType,
        @Positive int capacity,
        @NotNull @PositiveOrZero BigDecimal price
) {}
