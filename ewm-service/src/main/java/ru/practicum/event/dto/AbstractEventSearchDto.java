package ru.practicum.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@FieldDefaults(level = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public abstract class AbstractEventSearchDto {
    List<Long> categoriesIds;
    Instant rangeStart;
    Instant rangeEnd;
    Long locationId;
    Float lat;
    Float lon;
    Float radius;
    Integer from;
    Integer size;
}
