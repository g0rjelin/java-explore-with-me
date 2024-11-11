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
public class EventAdminSearchDto {
    List<Long> usersIds;
    List<String> states;
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