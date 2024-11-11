package ru.practicum.location.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class LocationFullDto {
    long id;
    float lat;
    float lon;
    String name;
    float radius; //километры
}