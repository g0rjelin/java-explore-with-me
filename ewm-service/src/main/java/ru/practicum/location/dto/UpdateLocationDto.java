package ru.practicum.location.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class UpdateLocationDto {
    @Min(-90)
    @Max(90)
    Float lat;
    @Min(-180)
    @Max(180)
    Float lon;
    @Size(min = 1, max = 100)
    String name;
    @Min(0)
    Float radius; //километры
}
