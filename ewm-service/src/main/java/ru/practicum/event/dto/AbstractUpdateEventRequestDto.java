package ru.practicum.event.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.location.dto.LocationDto;

import java.time.Instant;

@Data
public abstract class AbstractUpdateEventRequestDto {
    @Size(min = 20, max = 2000)
    String annotation;

    @Positive
    Long categoryId;

    @Size(min = 20, max = 7000)
    String description;

    Instant eventDate;

    LocationDto location;

    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;

    @Size(min = 3, max = 120)
    String title;
}
