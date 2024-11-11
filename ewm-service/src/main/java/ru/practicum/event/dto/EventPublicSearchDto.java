package ru.practicum.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventPublicSearchDto {
    String text;
    List<Long> categoriesIds;
    Boolean paid;
    Instant rangeStart;
    Instant rangeEnd;
    Boolean onlyAvailable;
    EventSort eventSort;
    Long locationId;
    Float lat;
    Float lon;
    Float radius;
    Integer from;
    Integer size;
}