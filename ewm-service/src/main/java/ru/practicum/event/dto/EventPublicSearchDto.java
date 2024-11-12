package ru.practicum.event.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class EventPublicSearchDto extends AbstractEventSearchDto {
    final String text;
    final Boolean paid;
    final Boolean onlyAvailable;
    final EventSort eventSort;

    public EventPublicSearchDto(String text,
                                Boolean paid,
                                Boolean onlyAvailable,
                                EventSort eventSort,
                                List<Long> categoriesIds,
                                Instant rangeStart,
                                Instant rangeEnd,
                                Long locationId,
                                Float lat,
                                Float lon,
                                Float radius,
                                Integer from,
                                Integer size) {
        super(categoriesIds, rangeStart, rangeEnd, locationId, lat, lon, radius, from, size);
        this.text = text;
        this.paid = paid;
        this.onlyAvailable = onlyAvailable;
        this.eventSort = eventSort;
    }
}