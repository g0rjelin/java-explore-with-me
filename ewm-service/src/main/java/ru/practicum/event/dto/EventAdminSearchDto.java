package ru.practicum.event.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class EventAdminSearchDto extends AbstractEventSearchDto {
    final List<Long> usersIds;
    final List<String> states;

    public EventAdminSearchDto(List<Long> userIds,
                               List<String> states,
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
        this.usersIds = userIds;
        this.states = states;
    }
}