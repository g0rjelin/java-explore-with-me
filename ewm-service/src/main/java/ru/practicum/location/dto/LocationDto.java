package ru.practicum.location.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class LocationDto {
    @Min(-90)
    @Max(90)
    float lat;
    @Min(-180)
    @Max(180)
    float lon;
}