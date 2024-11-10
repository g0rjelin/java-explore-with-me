package ru.practicum.location.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class NewLocationDto {
    @Min(-90)
    @Max(90)
    @NotNull
    Float lat;
    @Min(-180)
    @Max(180)
    @NotNull
    Float lon;
    @Size(min = 1, max = 100)
    @NotBlank
    String name;
    @Min(0)
    float radius; //километры
}
