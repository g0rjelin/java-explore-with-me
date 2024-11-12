package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.validation.FutureHours;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;

    @NotBlank
    @Size(min = 20, max = 7000)
    String description;

    @Positive
    long category;

    @FutureHours(hours = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Instant eventDate;

    LocationDto location;

    boolean paid;

    @PositiveOrZero
    int participantLimit;

    boolean requestModeration = true;

    @Size(min = 3, max = 120)
    String title;
}
